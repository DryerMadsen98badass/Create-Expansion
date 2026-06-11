package net.mads.createexpansion.integration.jei;

import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.multiblock.MultiblockAbility;
import net.mads.createexpansion.multiblock.MultiblockDefinition;
import net.mads.createexpansion.multiblock.MultiblockPattern;
import net.mads.createexpansion.multiblock.MultiblockPredicate;
import net.mads.createexpansion.multiblock.MultiblockVisualization;
import net.mads.createexpansion.multiblock.PatternVariant;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiblockJeiRecipe {
    private final MultiblockDefinition definition;
    private int variantIndex;
    private int layerIndex = -1;
    private int tierIndex;
    private float viewYaw = 45.0F;
    private float viewPitch = 30.0F;
    private float zoom = 1.0F;
    private SelectedBlock selectedBlock;
    private int selectedStackPage;
    private int materialPage;

    public MultiblockJeiRecipe(MultiblockDefinition definition) {
        this.definition = definition;
        this.variantIndex = firstVariantIndex(definition);
    }

    public MultiblockDefinition definition() {
        return definition;
    }

    public PatternVariant variant() {
        return definition.variants().get(Math.floorMod(variantIndex, definition.variants().size()));
    }

    public MachineTier tier() {
        List<MachineTier> tiers = tiers();
        return tiers.get(Math.floorMod(tierIndex, tiers.size()));
    }

    public List<MachineTier> tiers() {
        List<MachineTier> tiers = definition.visualization().tiers();
        return tiers.isEmpty() ? MachineTier.ALL : tiers;
    }

    public int layerIndex() {
        return Math.min(Math.max(layerIndex, 0), Math.max(variant().width() - 1, 0));
    }

    public boolean allLayers() {
        return layerIndex < 0;
    }

    public boolean isLayerVisible(int x) {
        return allLayers() || x == layerIndex();
    }

    public String layerDisplay() {
        return allLayers() ? "All layers" : "Layer " + (layerIndex() + 1) + "/" + Math.max(variant().width(), 1);
    }

    public float viewYaw() {
        return viewYaw;
    }

    public float viewPitch() {
        return viewPitch;
    }

    public float zoom() {
        return zoom;
    }

    public SelectedBlock selectedBlock() {
        return selectedBlock;
    }

    public Character selectedSymbol() {
        return selectedBlock == null ? null : selectedBlock.symbol();
    }

    public int selectedStackPage() {
        return selectedStackPage;
    }

    public void nextLayer() {
        int width = variant().width();
        if (width <= 0) {
            layerIndex = -1;
            return;
        }

        layerIndex++;
        if (layerIndex >= width) {
            layerIndex = -1;
        }
        selectedBlock = null;
        selectedStackPage = 0;
        materialPage = 0;
    }

    public void nextVariant() {
        variantIndex = Math.floorMod(variantIndex + 1, definition.variants().size());
        if (layerIndex >= variant().width()) {
            layerIndex = -1;
        }
        selectedBlock = null;
        selectedStackPage = 0;
        materialPage = 0;
    }

    public void nextTier() {
        tierIndex = Math.floorMod(tierIndex + 1, tiers().size());
        selectedStackPage = 0;
        materialPage = 0;
    }

    public void resetView() {
        viewYaw = 45.0F;
        viewPitch = 30.0F;
        zoom = 1.0F;
    }

    public void dragView(double dragX, double dragY) {
        viewYaw += (float) dragX * 0.7F;
        viewPitch = clamp(viewPitch + (float) dragY * 0.7F, -65.0F, 65.0F);
    }

    public void zoom(double scrollDeltaY) {
        float factor = scrollDeltaY > 0.0D ? 1.12F : 0.88F;
        zoom = clamp(zoom * factor, 0.45F, 2.5F);
    }

    public void selectBlock(int x, int y, int z, char symbol) {
        selectedBlock = symbol == MultiblockPattern.air ? null : new SelectedBlock(x, y, z, symbol);
        selectedStackPage = 0;
    }

    public void nextSelectedStackPage(int pageSize) {
        if (selectedBlock == null || pageSize <= 0) {
            selectedStackPage = 0;
            return;
        }

        int pages = Math.max(1, (validStacks(selectedBlock.symbol()).size() + pageSize - 1) / pageSize);
        selectedStackPage = Math.floorMod(selectedStackPage + 1, pages);
    }

    public void nextMaterialPage(int pageSize) {
        if (selectedBlock != null || pageSize <= 0) {
            return;
        }
        int pages = Math.max(1, (materialEntries().size() + pageSize - 1) / pageSize);
        materialPage = Math.floorMod(materialPage + 1, pages);
    }

    public int materialPage(int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        int pages = Math.max(1, (materialEntries().size() + pageSize - 1) / pageSize);
        return Math.min(materialPage, pages - 1);
    }

    public List<ItemStack> validStacks(char symbol) {
        return definition.visualization().validStacks(symbol, tier(), definition.controller());
    }

    public List<ItemStack> allValidStacks() {
        Set<String> seen = new LinkedHashSet<>();
        List<ItemStack> stacks = new ArrayList<>();
        PatternVariant variant = variant();
        for (int x = 0; x < variant.width(); x++) {
            for (int y = 0; y < variant.height(); y++) {
                for (int z = 0; z < variant.length(); z++) {
                    addStacks(stacks, seen, definition.visualization().validStacks(variant.symbolAt(x, y, z), tier(), definition.controller()));
                }
            }
        }
        return stacks;
    }

    public List<MaterialEntry> materialEntries() {
        PatternVariant variant = variant();
        Map<Character, Integer> symbolCounts = new LinkedHashMap<>();
        for (int x = 0; x < variant.width(); x++) {
            for (int y = 0; y < variant.height(); y++) {
                for (int z = 0; z < variant.length(); z++) {
                    char symbol = variant.symbolAt(x, y, z);
                    if (symbol != MultiblockPattern.air) {
                        symbolCounts.merge(symbol, 1, Integer::sum);
                    }
                }
            }
        }

        Map<String, MaterialEntry> entries = new LinkedHashMap<>();
        for (Map.Entry<Character, Integer> symbolCount : symbolCounts.entrySet()) {
            char symbol = symbolCount.getKey();
            int count = symbolCount.getValue();
            List<ItemStack> validStacks = validStacks(symbol);
            MultiblockVisualization.SymbolInfo info = definition.visualization().symbols().get(symbol);
            List<MultiblockPredicate.CountRequirement> countRequirements = definition.countRequirements(symbol);
            for (MultiblockPredicate.CountRequirement requirement : countRequirements) {
                ItemStack stack = stackFor(requirement.blockId());
                if (!stack.isEmpty()) {
                    if (requirement.hasMinimum()) {
                        addMaterial(entries, List.of(stack), requirement.min(), false, countTooltip(requirement, requirement.min()), "min:" + requirement.key());
                    }
                    if (requirement.hasMaximum()) {
                        int total = Math.min(count, requirement.max());
                        addMaterial(entries, List.of(stack), total, false, countTooltip(requirement, total), "max:" + requirement.key());
                    }
                }
            }

            if (validStacks.isEmpty()) {
                continue;
            }

            if (isAbilitySymbol(info)) {
                addAbilityEntries(entries, info, validStacks);
            }

            int blockCount = count - abilityCount(info);
            if (blockCount > 0 && hasConcreteBlocks(info) && countRequirements.isEmpty()) {
                addMaterial(entries, List.of(firstConcreteStack(info, validStacks)), blockCount, false, List.of("Total: " + blockCount), "block:" + itemKey(firstConcreteStack(info, validStacks)));
            }
        }
        return List.copyOf(entries.values());
    }

    public int materialBlockCount() {
        PatternVariant variant = variant();
        int count = 0;
        for (int x = 0; x < variant.width(); x++) {
            for (int y = 0; y < variant.height(); y++) {
                for (int z = 0; z < variant.length(); z++) {
                    if (variant.symbolAt(x, y, z) != MultiblockPattern.air) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static void addStacks(List<ItemStack> stacks, Set<String> seen, List<ItemStack> candidates) {
        for (ItemStack stack : candidates) {
            String key = itemKey(stack);
            if (seen.add(key)) {
                stacks.add(stack);
            }
        }
    }

    private static boolean isAbilitySymbol(MultiblockVisualization.SymbolInfo info) {
        return info != null && (!info.requiredAbilities().isEmpty() || !info.anyAbilities().isEmpty());
    }

    private static int abilityCount(MultiblockVisualization.SymbolInfo info) {
        if (info == null) {
            return 0;
        }
        Set<MultiblockAbility> abilities = new LinkedHashSet<>();
        abilities.addAll(info.requiredAbilities());
        abilities.addAll(info.anyAbilities());
        return abilities.size();
    }

    private static boolean hasConcreteBlocks(MultiblockVisualization.SymbolInfo info) {
        return info == null || !info.blockIds().isEmpty() || !info.tieredBlocks().isEmpty() || info.tieredMachineCasing();
    }

    private static ItemStack firstConcreteStack(MultiblockVisualization.SymbolInfo info, List<ItemStack> validStacks) {
        if (info == null) {
            return validStacks.getFirst();
        }
        for (ItemStack stack : validStacks) {
            if (!(stack.getItem() instanceof BlockItem blockItem)
                    || !(blockItem.getBlock() instanceof net.mads.createexpansion.machine.MachinePortBlock)) {
                return stack;
            }
        }
        return validStacks.getFirst();
    }

    private static void addAbilityEntries(Map<String, MaterialEntry> entries, MultiblockVisualization.SymbolInfo info, List<ItemStack> validStacks) {
        Set<MultiblockAbility> abilities = new LinkedHashSet<>();
        abilities.addAll(info.requiredAbilities());
        abilities.addAll(info.anyAbilities());
        for (MultiblockAbility ability : abilities) {
            List<ItemStack> stacks = validStacks.stream()
                    .filter(stack -> stack.getItem() instanceof BlockItem blockItem
                            && blockItem.getBlock() instanceof net.mads.createexpansion.machine.MachinePortBlock port
                            && port.abilities().contains(ability))
                    .toList();
            if (!stacks.isEmpty()) {
                addMaterial(entries, stacks, 1, true, List.of("Ability: " + abilityLabel(ability)), "ability:" + ability.name());
            }
        }
    }

    private static String abilityLabel(MultiblockAbility ability) {
        return switch (ability) {
            case ITEM_INPUT -> "Input";
            case ITEM_OUTPUT -> "Output";
            case FLUID_INPUT -> "Fluid In";
            case FLUID_OUTPUT -> "Fluid Out";
            case ENERGY_INPUT -> "Energy In";
            case ENERGY_OUTPUT -> "Energy Out";
            case KINETIC_INPUT -> "Kinetic In";
            case KINETIC_OUTPUT -> "Kinetic Out";
            case IO_INTERFACE -> "I/O";
            case MUFFLER -> "Muffler";
            case REDSTONE -> "Redstone";
        };
    }

    private static List<String> countTooltip(MultiblockPredicate.CountRequirement requirement, int total) {
        List<String> lines = new ArrayList<>();
        lines.add("Total: " + total);
        if (requirement.hasMinimum()) {
            lines.add("Minimum: " + requirement.min());
        }
        if (requirement.hasMaximum()) {
            lines.add("Maximum: " + requirement.max());
        }
        return lines;
    }

    private static void addMaterial(Map<String, MaterialEntry> entries, List<ItemStack> stacks, int count, boolean ability, List<String> tooltip, String key) {
        if (stacks.isEmpty()) {
            return;
        }
        entries.putIfAbsent(key, new MaterialEntry(stacks.stream().map(stack -> withCount(stack, count)).toList(), ability, List.copyOf(tooltip)));
    }

    private static ItemStack withCount(ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        copy.setCount(Math.max(1, count));
        return copy;
    }

    private static ItemStack stackFor(net.minecraft.resources.ResourceLocation blockId) {
        Block block = BuiltInRegistries.BLOCK.get(blockId);
        ItemStack stack = new ItemStack(block);
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    private static String itemKey(ItemStack stack) {
        return stack.getItem().builtInRegistryHolder().key().location().toString();
    }

    private static int firstVariantIndex(MultiblockDefinition definition) {
        int bestIndex = 0;
        int bestLevel = Integer.MAX_VALUE;
        for (int i = 0; i < definition.variants().size(); i++) {
            int level = definition.variants().get(i).variantLevel();
            if (level > 0 && level < bestLevel) {
                bestLevel = level;
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public record SelectedBlock(int x, int y, int z, char symbol) {
    }

    public record MaterialEntry(List<ItemStack> stacks, boolean ability, List<String> tooltip) {
        public ItemStack stack() {
            return stacks.isEmpty() ? ItemStack.EMPTY : stacks.getFirst();
        }
    }
}
