package net.mads.createexpansion.machine.machines.electric.multiblock;

import net.mads.createexpansion.gui.ProgressBar;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.coil.CoilBlock;
import net.mads.createexpansion.machine.coil.CoilLogic;
import net.mads.createexpansion.machine.interaction.BlockInteraction;
import net.mads.createexpansion.machine.interaction.MachineCondition;
import net.mads.createexpansion.machine.interaction.MachineModifier;
import net.mads.createexpansion.recipe.CERecipeLogicDefinition;
import net.mads.createexpansion.recipe.CERecipeTypes;
import net.mads.createexpansion.recipe.RecipeTypeDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class MultiblockDefinition {
    private final String id;
    private final String displayName;
    private final MultiblockControllerDefinition controllerDefinition;
    private final ResourceLocation controllerId;
    private final List<ResourceLocation> recipeTypes;
    private final List<ResourceLocation> logicIds;
    private final List<PatternVariant> variants;
    private final Map<Character, MultiblockPredicate> predicates;
    private final Set<MultiblockAbility> requiredRecipeAbilities;
    private final List<String> tooltip;
    private final boolean externalHeatSource;
    private final InputOnlyDisplay inputOnlyDisplay;
    private final char controllerSymbol;
    private final MultiblockVisualization visualization;
    private final List<WorldInteraction> worldInteractions;
    private final List<BlockInteraction> blockInteractions;
    private final List<MachineCondition> conditions;
    private final List<MachineModifier> modifiers;
    private final ProgressBar progressBar;

    private MultiblockDefinition(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.controllerDefinition = builder.controllerDefinition;
        this.controllerId = builder.controllerId;
        this.recipeTypes = List.copyOf(builder.recipeTypes);
        this.logicIds = List.copyOf(builder.logicIds);
        this.predicates = Map.copyOf(builder.predicates);
        this.requiredRecipeAbilities = builder.requiredRecipeAbilities.isEmpty() ? Set.of() : EnumSet.copyOf(builder.requiredRecipeAbilities);
        this.tooltip = List.copyOf(builder.tooltip);
        this.externalHeatSource = builder.externalHeatSource;
        this.inputOnlyDisplay = builder.inputOnlyDisplay;
        this.controllerSymbol = builder.controllerSymbol;
        this.visualization = builder.visualization;
        this.worldInteractions = List.copyOf(builder.worldInteractions);
        this.blockInteractions = List.copyOf(builder.blockInteractions);
        this.conditions = List.copyOf(builder.conditions);
        this.modifiers = List.copyOf(builder.modifiers);
        this.progressBar = builder.progressBar != null
                ? builder.progressBar
                : builder.recipeTypeDefinitions.stream()
                .map(RecipeTypeDefinition::progressBar)
                .findFirst()
                .orElse(ProgressBar.ARROW);
        this.variants = builder.variants.stream()
                .sorted(Comparator.comparingInt(PatternVariant::variantLevel).reversed())
                .toList();
    }

    public static Builder machine() {
        return new Builder();
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public ResourceLocation controllerId() {
        return controllerId;
    }

    public MultiblockControllerDefinition controller() {
        return controllerDefinition;
    }

    public List<ResourceLocation> recipeTypes() {
        return recipeTypes;
    }

    public List<ResourceLocation> logicIds() {
        return logicIds;
    }

    public List<PatternVariant> variants() {
        return variants;
    }

    public MultiblockVisualization visualization() {
        return visualization;
    }

    public List<WorldInteraction> worldInteractions() {
        return worldInteractions;
    }

    public List<BlockInteraction> blockInteractions() {
        return blockInteractions;
    }

    public List<MachineCondition> conditions() {
        return conditions;
    }

    public List<MachineModifier> modifiers() {
        return modifiers;
    }

    public List<BlockPos> worldInteractionPositions(
            BlockPos controllerPos,
            Direction facing,
            WorldInteractionType type
    ) {
        return worldInteractions.stream()
                .filter(interaction -> interaction.type() == type)
                .map(interaction -> rotate(
                        controllerPos,
                        facing,
                        interaction.x(),
                        interaction.y(),
                        interaction.z()
                ))
                .toList();
    }

    public List<MultiblockPredicate.CountRequirement> countRequirements(char symbol) {
        MultiblockPredicate predicate = predicates.get(symbol);
        return predicate == null ? List.of() : predicate.countRequirements();
    }

    public Set<MultiblockAbility> requiredRecipeAbilities() {
        return requiredRecipeAbilities;
    }

    public List<String> tooltip() {
        return tooltip;
    }

    public boolean externalHeatSource() {
        return externalHeatSource;
    }

    public InputOnlyDisplay inputOnlyDisplay() {
        return inputOnlyDisplay;
    }

    public ProgressBar progressBar() {
        return progressBar;
    }

    public MultiblockMatchResult tryMatch(Level level, BlockPos controllerPos, Direction facing) {
        for (PatternVariant variant : variants) {
            MultiblockMatchResult result = tryMatchVariant(level, controllerPos, facing, variant);
            if (result.matched()) {
                return result;
            }
        }

        return MultiblockMatchResult.failed();
    }

    private MultiblockMatchResult tryMatchVariant(Level level, BlockPos controllerPos, Direction facing, PatternVariant variant) {
        for (LocalPos controllerLocalPos : findControllerPositions(variant)) {
            MultiblockMatchResult result = matchFromController(level, controllerPos, facing, variant, controllerLocalPos);
            if (result.matched()) {
                return result;
            }
        }

        return MultiblockMatchResult.failed();
    }

    private List<LocalPos> findControllerPositions(PatternVariant variant) {
        List<LocalPos> positions = new ArrayList<>();
        for (int x = 0; x < variant.layers().size(); x++) {
            MultiblockPattern.Row[] rows = variant.layers().get(x);
            for (int y = 0; y < rows.length; y++) {
                char[] symbols = rows[y].symbols();
                for (int z = 0; z < symbols.length; z++) {
                    if (symbols[z] == controllerSymbol) {
                        positions.add(new LocalPos(x, y, z));
                    }
                }
            }
        }

        if (positions.isEmpty()) {
            positions.add(new LocalPos(0, 0, 0));
        }
        return positions;
    }

    private MultiblockMatchResult matchFromController(Level level, BlockPos controllerPos, Direction facing, PatternVariant variant, LocalPos controllerLocalPos) {
        MachineTier formedTier = null;
        List<BlockPos> positions = new ArrayList<>();
        Map<MultiblockAbility, List<BlockPos>> abilityPositions = new EnumMap<>(MultiblockAbility.class);
        Map<String, Integer> countMatches = new HashMap<>();
        Map<BlockPos, ResourceLocation> overlays = new HashMap<>();

        for (int x = 0; x < variant.layers().size(); x++) {
            MultiblockPattern.Row[] rows = variant.layers().get(x);
            for (int y = 0; y < rows.length; y++) {
                char[] symbols = rows[y].symbols();
                for (int z = 0; z < symbols.length; z++) {
                    char symbol = symbols[z];
                    BlockPos worldPos = rotate(controllerPos, facing, x - controllerLocalPos.x, y - controllerLocalPos.y, z - controllerLocalPos.z);
                    MultiblockPredicate predicate = symbol == controllerSymbol ? MultiblockPredicates.block(controllerId.toString()) : predicates.get(symbol);
                    if (predicate == null) {
                        return MultiblockMatchResult.failed();
                    }

                    BlockState state = level.getBlockState(worldPos);
                    MultiblockPredicate.Match match = predicate.match(level, worldPos, state);
                    if (!match.matches()) {
                        return MultiblockMatchResult.failed();
                    }

                    positions.add(worldPos);
                    formedTier = MultiblockPredicates.lowestTier(formedTier, match.tier());
                    for (MultiblockAbility ability : match.abilities()) {
                        abilityPositions.computeIfAbsent(ability, ignored -> new ArrayList<>()).add(worldPos);
                    }
                    match.counts().forEach((key, count) -> countMatches.merge(key, count, Integer::sum));
                    if (match.overlayTexture() != null) {
                        overlays.put(worldPos, match.overlayTexture());
                    }
                }
            }
        }

        if (!hasRequiredRecipeAbilities(abilityPositions)) {
            return MultiblockMatchResult.failed();
        }
        if (!hasRequiredCounts(countMatches)) {
            return MultiblockMatchResult.failed();
        }

        CoilInfo coilInfo = coilInfo(level, positions);
        if (requiresCoils() && !coilInfo.valid()) {
            return MultiblockMatchResult.failed();
        }

        return new MultiblockMatchResult(true, variant.id(), variant.variantLevel(), formedTier, coilInfo.heat(), coilInfo.count(), List.copyOf(positions), copyAbilities(abilityPositions), Map.copyOf(overlays));
    }

    private boolean requiresCoils() {
        return logicIds.contains(CoilLogic.TEMP.id());
    }

    private CoilInfo coilInfo(Level level, List<BlockPos> positions) {
        String coilId = null;
        int heat = 0;
        int count = 0;
        for (BlockPos pos : positions) {
            if (!(level.getBlockState(pos).getBlock() instanceof CoilBlock coil)) {
                continue;
            }

            if (coilId == null) {
                coilId = coil.definition().id();
                heat = coil.definition().heat();
            } else if (!coilId.equals(coil.definition().id())) {
                return CoilInfo.invalid();
            }

            count++;
        }
        return new CoilInfo(heat, count, count > 0);
    }

    private record CoilInfo(int heat, int count, boolean valid) {
        private static CoilInfo invalid() {
            return new CoilInfo(0, 0, false);
        }
    }

    private boolean hasRequiredRecipeAbilities(Map<MultiblockAbility, List<BlockPos>> abilityPositions) {
        for (MultiblockAbility ability : requiredRecipeAbilities) {
            if (!abilityPositions.containsKey(ability)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasRequiredCounts(Map<String, Integer> countMatches) {
        for (MultiblockPredicate predicate : predicates.values()) {
            for (MultiblockPredicate.CountRequirement requirement : predicate.countRequirements()) {
                int count = countMatches.getOrDefault(requirement.key(), 0);
                if (requirement.hasMinimum() && count < requirement.min()) {
                    return false;
                }
                if (requirement.hasMaximum() && count > requirement.max()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static Map<MultiblockAbility, List<BlockPos>> copyAbilities(Map<MultiblockAbility, List<BlockPos>> abilityPositions) {
        Map<MultiblockAbility, List<BlockPos>> copied = new EnumMap<>(MultiblockAbility.class);
        abilityPositions.forEach((ability, positions) -> copied.put(ability, List.copyOf(positions)));
        return copied;
    }

    private static BlockPos rotate(BlockPos origin, Direction facing, int localX, int localY, int localZ) {
        Direction right = facing.getClockWise();
        Direction forward = facing.getOpposite();
        return origin
                .relative(right, localX)
                .above(localY)
                .relative(forward, localZ);
    }

    private record LocalPos(int x, int y, int z) {
    }

    public enum WorldInteractionType {
        INPUT,
        OUTPUT;

        public static WorldInteractionType fromName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("World interaction type cannot be null");
            }

            return switch (name.trim().toLowerCase()) {
                case "input" -> INPUT;
                case "output" -> OUTPUT;
                default -> throw new IllegalArgumentException(
                        "Unknown world interaction type: " + name + ". Expected input or output."
                );
            };
        }
    }

    public record WorldInteraction(
            WorldInteractionType type,
            int x,
            int y,
            int z
    ) {
    }

    public record InputOnlyDisplay(int durationTicks, int cePerTick, List<ItemInput> itemInputs, List<FluidInput> fluidInputs) {
        public InputOnlyDisplay {
            durationTicks = Math.max(1, durationTicks);
            itemInputs = List.copyOf(itemInputs);
            fluidInputs = List.copyOf(fluidInputs);
        }

        public boolean dynamicCePerTick() {
            return cePerTick < 0;
        }
    }

    public record ItemInput(ResourceLocation itemId, int amount) {
    }

    public record FluidInput(ResourceLocation fluidId, int amount) {
    }

    /**
     * Options shown by autocomplete inside {@code .machineDefinition(Option...)} for multiblocks.
     */
    @FunctionalInterface
    public interface Option {
        void apply(Builder builder);

        /** Defines the multiblock registry path. */
        static Option id(String id) {
            return builder -> builder.id(id);
        }

        /** Defines the user-facing controller and structure name. */
        static Option displayName(String displayName) {
            return builder -> builder.displayName(displayName);
        }

        /** Adds one or more tooltip lines to the controller item. */
        static Option tooltip(String... lines) {
            return builder -> builder.tooltip(lines);
        }

        /** Selects an already registered controller definition. */
        static Option controller(MultiblockControllerDefinition controller) {
            return builder -> builder.controller(controller);
        }

        /** Selects a controller by registry id. */
        static Option controller(String controllerId) {
            return builder -> builder.controller(controllerId);
        }

        /** Overrides the character used for the controller in every pattern variant. */
        static Option controllerSymbol(char controllerSymbol) {
            return builder -> builder.controllerSymbol(controllerSymbol);
        }

        /** Adds a CE recipe type processed by this multiblock. */
        static Option recipeType(RecipeTypeDefinition recipeType) {
            return builder -> builder.recipeType(recipeType);
        }

        /** Adds multiple CE recipe types processed by this multiblock. */
        static Option recipeTypes(RecipeTypeDefinition... recipeTypes) {
            return builder -> builder.recipeTypes(recipeTypes);
        }

        /** Adds a supported custom recipe-logic capability. */
        static Option logic(CERecipeLogicDefinition logic) {
            return builder -> builder.logic(logic);
        }

        /**
         * Marks the multiblock as an external heat provider. It uses the special request-driven
         * heat lifecycle instead of searching for a CE recipe by itself.
         */
        static Option externalHeatSource() {
            return Builder::externalHeatSource;
        }

        /** Defines a display-only operation for a multiblock without CE recipe outputs. */
        static Option inputOnly(int durationTicks) {
            return builder -> builder.inputOnly(durationTicks);
        }

        /** Defines a display-only operation with an explicit CE/t value. */
        static Option inputOnly(int durationTicks, int cePerTick) {
            return builder -> builder.inputOnly(durationTicks, cePerTick);
        }

        /** Adds an item displayed as an input for an input-only operation. */
        static Option inputItem(String itemId, int amount) {
            return builder -> builder.inputItem(itemId, amount);
        }

        /** Adds a fluid displayed as an input for an input-only operation. */
        static Option inputFluid(String fluidId, int amount) {
            return builder -> builder.inputFluid(fluidId, amount);
        }

        /**
         * Adds a controller-relative world IO position. Coordinates rotate with the controller;
         * {@code 0, 0, 0} is the controller itself.
         */
        static Option worldInteraction(WorldInteractionType type, int x, int y, int z) {
            return builder -> builder.worldInteraction(type, x, y, z);
        }

        /** Adds a world block/fluid interaction that applies to every recipe this multiblock runs. */
        static Option blockInteraction(BlockInteraction interaction) {
            return builder -> builder.blockInteractions.add(Objects.requireNonNull(interaction));
        }

        /** Adds a world block/fluid interaction that applies to every recipe this multiblock runs. */
        static Option blockInteraction(BlockInteraction.Builder interaction) {
            return blockInteraction(Objects.requireNonNull(interaction).build());
        }

        /** Adds a world condition that applies to every recipe this multiblock runs. */
        static Option condition(MachineCondition condition) {
            return builder -> builder.conditions.add(Objects.requireNonNull(condition));
        }

        /** Adds an ordered modifier that applies to recipes this multiblock runs. */
        static Option modifier(MachineModifier modifier) {
            return builder -> builder.modifiers.add(Objects.requireNonNull(modifier));
        }

        /** Adds an ordered modifier that applies to recipes this multiblock runs. */
        static Option modifier(MachineModifier.Builder modifier) {
            return modifier(Objects.requireNonNull(modifier).build());
        }

        /**
         * Adds one complete pattern variant. Layers and rows retain the existing layout syntax,
         * and the variant id controls variant ordering.
         */
        static Option variant(String id, Consumer<MultiblockPattern.VariantBuilder> pattern) {
            return builder -> builder.variant(id, pattern);
        }

        /** Adds generated structure-visualization metadata. */
        static Option visualization(Consumer<MultiblockVisualization.Builder> visualization) {
            return builder -> builder.visualization(visualization);
        }

        /**
         * Maps a pattern symbol to its block, ability, coil, or composite predicate.
         * Predicate count limits are validated while the structure forms.
         */
        static Option where(char symbol, MultiblockPredicate predicate) {
            return builder -> builder.where(symbol, predicate);
        }

        /** Maps a pattern symbol directly to a block id. */
        static Option where(char symbol, String blockId) {
            return builder -> builder.where(symbol, blockId);
        }

        /** Maps a pattern symbol to one required multiblock ability. */
        static Option where(char symbol, MultiblockAbility ability) {
            return builder -> builder.where(symbol, ability);
        }

        /** Maps a pattern symbol to any one of the supplied multiblock abilities. */
        static Option where(char symbol, MultiblockAbility first, MultiblockAbility... extra) {
            return builder -> builder.where(symbol, first, extra);
        }

        /**
         * Overrides the recipe type's default progress bar in this controller GUI.
         */
        static Option progressBar(ProgressBar progressBar) {
            return builder -> builder.progressBar = Objects.requireNonNull(progressBar);
        }
    }

    public static final class Builder {
        private String id;
        private String displayName;
        private MultiblockControllerDefinition controllerDefinition;
        private ResourceLocation controllerId;
        private final List<ResourceLocation> recipeTypes = new ArrayList<>();
        private final List<RecipeTypeDefinition> recipeTypeDefinitions = new ArrayList<>();
        private final List<ResourceLocation> logicIds = new ArrayList<>();
        private final List<PatternVariant> variants = new ArrayList<>();
        private final Map<Character, MultiblockPredicate> rawPredicates = new HashMap<>();
        private final Map<Character, MultiblockPredicate> predicates = new HashMap<>();
        private final Set<MultiblockAbility> requiredRecipeAbilities = EnumSet.noneOf(MultiblockAbility.class);
        private final List<String> tooltip = new ArrayList<>();
        private boolean externalHeatSource;
        private InputOnlyDisplay inputOnlyDisplay;
        private char controllerSymbol = MultiblockPattern.controller;
        private MultiblockVisualization visualization = MultiblockVisualization.empty();
        private final List<WorldInteraction> worldInteractions = new ArrayList<>();
        private final List<BlockInteraction> blockInteractions = new ArrayList<>();
        private final List<MachineCondition> conditions = new ArrayList<>();
        private final List<MachineModifier> modifiers = new ArrayList<>();
        private ProgressBar progressBar;

        private Builder() {
        }

        public Builder machineDefinition(Option option) {
            Objects.requireNonNull(option, "Multiblock machine option").apply(this);
            return this;
        }

        private Builder id(String id) {
            this.id = id;
            if (displayName == null) {
                displayName = id;
            }
            return this;
        }

        private Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        private Builder tooltip(String... lines) {
            this.tooltip.addAll(List.of(lines));
            return this;
        }

        private Builder externalHeatSource() {
            this.externalHeatSource = true;
            return this;
        }

        private Builder inputOnly(int durationTicks) {
            return inputOnly(durationTicks, -1);
        }

        private Builder inputOnly(int durationTicks, int cePerTick) {
            this.inputOnlyDisplay = new InputOnlyDisplay(durationTicks, cePerTick, List.of(), List.of());
            return this;
        }

        private Builder inputItem(String itemId, int amount) {
            InputOnlyDisplay display = inputOnlyDisplayOrDefault();
            List<ItemInput> items = new ArrayList<>(display.itemInputs());
            items.add(new ItemInput(MultiblockRegistry.id(itemId), Math.max(1, amount)));
            this.inputOnlyDisplay = new InputOnlyDisplay(display.durationTicks(), display.cePerTick(), items, display.fluidInputs());
            return this;
        }

        private Builder inputFluid(String fluidId, int amount) {
            InputOnlyDisplay display = inputOnlyDisplayOrDefault();
            List<FluidInput> fluids = new ArrayList<>(display.fluidInputs());
            fluids.add(new FluidInput(MultiblockRegistry.id(fluidId), Math.max(1, amount)));
            this.inputOnlyDisplay = new InputOnlyDisplay(display.durationTicks(), display.cePerTick(), display.itemInputs(), fluids);
            return this;
        }

        private Builder controller(String controllerId) {
            ResourceLocation id = MultiblockRegistry.id(controllerId);
            this.controllerId = id;
            this.controllerDefinition = MultiblockControllerDefinition.of(
                    id.getPath(),
                    id.getPath(),
                    "block/machines/ino/casing",
                    "block/machines/overlay/foundry/foundry_off",
                    "block/machines/overlay/foundry/foundry_on"
            );
            return this;
        }

        private Builder controller(MultiblockControllerDefinition controller) {
            this.controllerDefinition = controller;
            this.controllerId = controller.id();
            return this;
        }

        private Builder controllerSymbol(char controllerSymbol) {
            this.controllerSymbol = controllerSymbol;
            return this;
        }

        private Builder recipeType(ResourceLocation recipeType) {
            this.recipeTypes.add(recipeType);
            RecipeTypeDefinition knownType = CERecipeTypes.byId(recipeType);
            if (knownType != null && !this.recipeTypeDefinitions.contains(knownType)) {
                this.recipeTypeDefinitions.add(knownType);
            }
            return this;
        }

        private Builder recipeType(RecipeTypeDefinition recipeType) {
            this.recipeTypes.add(recipeType.id());
            if (!this.recipeTypeDefinitions.contains(recipeType)) {
                this.recipeTypeDefinitions.add(recipeType);
            }
            return this;
        }

        private Builder recipeType(String recipeType) {
            return recipeType(MultiblockRegistry.id(recipeType));
        }

        private Builder recipeTypes(ResourceLocation... recipeTypes) {
            for (ResourceLocation recipeType : recipeTypes) {
                recipeType(recipeType);
            }
            return this;
        }

        private Builder recipeTypes(RecipeTypeDefinition... recipeTypes) {
            for (RecipeTypeDefinition recipeType : recipeTypes) {
                recipeType(recipeType);
            }
            return this;
        }

        private Builder recipeTypes(String... recipeTypes) {
            for (String recipeType : recipeTypes) {
                recipeType(recipeType);
            }
            return this;
        }

        private Builder worldInteraction(String type, int x, int y, int z) {
            return worldInteraction(WorldInteractionType.fromName(type), x, y, z);
        }

        private Builder worldInteraction(WorldInteractionType type, int x, int y, int z) {
            if (type == null) {
                throw new IllegalArgumentException("World interaction type cannot be null");
            }
            this.worldInteractions.add(new WorldInteraction(type, x, y, z));
            return this;
        }

        private Builder logic(CERecipeLogicDefinition logic) {
            this.logicIds.add(logic.id());
            return this;
        }

        private Builder logic(String logicId) {
            this.logicIds.add(ResourceLocation.parse(logicId.contains(":") ? logicId : "create_expansion:" + logicId));
            return this;
        }

        private Builder variant(String id, Consumer<MultiblockPattern.VariantBuilder> builderConsumer) {
            MultiblockPattern.VariantBuilder patternBuilder = new MultiblockPattern.VariantBuilder();
            builderConsumer.accept(patternBuilder);
            this.variants.add(patternBuilder.build(id));
            return this;
        }

        private Builder visualization(Consumer<MultiblockVisualization.Builder> builderConsumer) {
            MultiblockVisualization.Builder builder = MultiblockVisualization.builder();
            builderConsumer.accept(builder);
            this.visualization = this.visualization.merge(builder.build());
            return this;
        }

        private Builder where(char symbol, MultiblockPredicate predicate) {
            this.rawPredicates.put(symbol, predicate);
            MultiblockVisualization.SymbolInfo info = predicate.visualizationInfo();
            if (info != null) {
                this.visualization = this.visualization.withSymbol(symbol, info);
            }
            return this;
        }

        private Builder where(char symbol, String blockId) {
            return where(symbol, MultiblockPredicates.block(blockId));
        }

        private Builder where(char symbol, MultiblockAbility ability) {
            return where(symbol, MultiblockPredicates.ability(ability));
        }

        private Builder where(char symbol, MultiblockAbility firstAbility, MultiblockAbility... extraAbilities) {
            MultiblockAbility[] abilities = new MultiblockAbility[extraAbilities.length + 1];
            abilities[0] = firstAbility;
            System.arraycopy(extraAbilities, 0, abilities, 1, extraAbilities.length);
            return where(symbol, MultiblockPredicates.anyAbility(abilities));
        }

        public MultiblockDefinition build() {
            if (id == null || id.isBlank() || !ResourceLocation.isValidPath(id)) {
                throw new IllegalStateException("Multiblock is missing a valid Option.id(...): " + id);
            }
            if (controllerId == null) {
                throw new IllegalStateException("Multiblock " + id + " is missing a controller");
            }
            if (variants.isEmpty()) {
                throw new IllegalStateException("Multiblock " + id + " has no variants");
            }
            resolvePredicates();
            return new MultiblockDefinition(this);
        }

        private InputOnlyDisplay inputOnlyDisplayOrDefault() {
            if (inputOnlyDisplay == null) {
                inputOnlyDisplay = new InputOnlyDisplay(20, -1, List.of(), List.of());
            }
            return inputOnlyDisplay;
        }

        private void resolvePredicates() {
            this.predicates.clear();
            this.requiredRecipeAbilities.clear();
            Set<MultiblockAbility> neededAbilities = neededRecipeAbilities();

            for (Map.Entry<Character, MultiblockPredicate> entry : rawPredicates.entrySet()) {
                MultiblockPredicate predicate = entry.getValue();
                if (predicate instanceof MultiblockPredicates.RecipeTypeAwarePredicate recipeTypeAwarePredicate) {
                    predicate = recipeTypeAwarePredicate.bindRecipeAbilities(neededAbilities);
                    if (recipeTypeAwarePredicate.requiresRecipeAbilities()) {
                        this.requiredRecipeAbilities.addAll(neededAbilities);
                    }
                }

                this.predicates.put(entry.getKey(), predicate);
                MultiblockVisualization.SymbolInfo info = predicate.visualizationInfo();
                if (info != null) {
                    this.visualization = this.visualization.withSymbol(entry.getKey(), info);
                }
            }
        }

        private Set<MultiblockAbility> neededRecipeAbilities() {
            EnumSet<MultiblockAbility> abilities = EnumSet.noneOf(MultiblockAbility.class);
            for (RecipeTypeDefinition recipeType : recipeTypeDefinitions) {
                if (recipeType.maxItemInputs() > 0) {
                    abilities.add(MultiblockAbility.ITEM_INPUT);
                }
                if (recipeType.maxItemOutputs() > 0) {
                    abilities.add(MultiblockAbility.ITEM_OUTPUT);
                }
                if (recipeType.maxFluidInputs() > 0) {
                    abilities.add(MultiblockAbility.FLUID_INPUT);
                }
                if (recipeType.maxFluidOutputs() > 0) {
                    abilities.add(MultiblockAbility.FLUID_OUTPUT);
                }
                switch (recipeType.kineticMode()) {
                    case CONSUMES -> abilities.add(MultiblockAbility.KINETIC_INPUT);
                    case GENERATES -> abilities.add(MultiblockAbility.KINETIC_OUTPUT);
                    case BOTH -> {
                        abilities.add(MultiblockAbility.KINETIC_INPUT);
                        abilities.add(MultiblockAbility.KINETIC_OUTPUT);
                    }
                    case NONE -> {
                    }
                }

                switch (recipeType.energyMode()) {
                    case CONSUMES -> abilities.add(MultiblockAbility.ENERGY_INPUT);
                    case GENERATES -> abilities.add(MultiblockAbility.ENERGY_OUTPUT);
                    case BOTH -> {
                        abilities.add(MultiblockAbility.ENERGY_INPUT);
                        abilities.add(MultiblockAbility.ENERGY_OUTPUT);
                    }
                    case NONE -> {
                    }
                }
            }
            return abilities;
        }
    }
}
