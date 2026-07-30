package net.mads.createexpansion.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.fluid.IndustrialFluid;
import net.mads.createexpansion.machine.MachineTierStats;
import net.mads.createexpansion.machine.coil.CoilBlock;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialSubstance;
import net.mads.createexpansion.material.MaterialComponent;
import net.mads.createexpansion.material.MaterialLookup;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID, value = Dist.CLIENT)
public final class ClientMaterialTooltip {
    private static int foundryTooltipPage;
    private static boolean wasFoundryTooltipLeftDown;
    private static boolean wasFoundryTooltipRightDown;

    private static final List<List<String>> FOUNDRY_TOOLTIP_PAGES = List.of(
            List.of(
                    "Structure:",
                    "Outer sizes: 3x3, 5x5, 7x7, or 9x9.",
                    "Minimum height is 2 blocks.",
                    "The bottom layer is the floor plate.",
                    "Inside height is total height - 1.",
                    "Capacity is 1296 mB per inside block."
            ),
            List.of(
                    "Heat and melting:",
                    "Heat comes from Blaze Burners or Large Heater below the inside footprint.",
                    "Temperature moves gradually by 1 C per second.",
                    "Items melt when Foundry temperature reaches their melting point.",
                    "Melting produces molten material fluid.",
                    "Valid alloys are resolved automatically from IndustrialMaterials."
            ),
            List.of(
                    "Input and output:",
                    "Input Bus inserts valid meltable items into all internal melting slots.",
                    "Input Hatch inserts molten fluids into the Foundry tank.",
                    "Output Hatch exposes the Foundry tank for draining.",
                    "Unrelated materials are rejected unless they fit the current alloy family.",
                    "Taller Foundries have more capacity and more melting slots."
            ),
            List.of(
                    "Casting:",
                    "A Drain pulls fluid from an Output Hatch into a Mold Caster below it.",
                    "The Mold Caster needs a mold item inserted first.",
                    "Molten fluid plus a mold creates the matching CAST_* item.",
                    "If poured fluid is hotter than the mold melting point, the mold and fluid are lost.",
                    "If poured fluid is above half the mold melting point, the mold becomes hot."
            )
    );

    private ClientMaterialTooltip() {
    }

    @SubscribeEvent
    public static void addMaterialTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == ItemRegistry.FOUNDRY_CONTROLLER.get()
                || event.getItemStack().getItem() == ItemRegistry.CREATIVE_FOUNDRY_CONTROLLER.get()) {
            addFoundryTooltipLines(event.getToolTip());
            return;
        }

        Player player = event.getEntity();
        if (player == null || !GogglesItem.isWearingGoggles(player)) {
            return;
        }

        MaterialLookup.MaterialTarget target =
                MaterialLookup.find(event.getItemStack());

        if (target != null) {
            addMaterialTooltipLines(event.getToolTip(), target);
        }

        if (event.getItemStack().getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof EnergyWireBlock wire) {
            addWireTooltipLines(event.getToolTip(), wire);
        }

        if (event.getItemStack().getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof CoilBlock coil) {
            addCoilTooltipLines(event.getToolTip(), coil);
        }
    }

    private static void addFoundryTooltipLines(List<Component> tooltip) {
        tooltip.add(
                Component.literal(
                        "Dynamic melting, alloying, and casting multiblock"
                ).withStyle(ChatFormatting.GRAY)
        );

        if (!Screen.hasShiftDown()) {
            tooltip.add(
                    Component.literal(
                            "Hold Shift for Foundry guide"
                    ).withStyle(ChatFormatting.DARK_GRAY)
            );
            return;
        }

        updateFoundryTooltipPage();

        List<String> page =
                FOUNDRY_TOOLTIP_PAGES.get(foundryTooltipPage);

        tooltip.add(
                Component.literal(
                        "Foundry Guide "
                                + (foundryTooltipPage + 1)
                                + "/"
                                + FOUNDRY_TOOLTIP_PAGES.size()
                ).withStyle(ChatFormatting.GOLD)
        );

        tooltip.add(
                Component.literal(
                        "Use Left/Right Arrow to change page"
                ).withStyle(ChatFormatting.DARK_GRAY)
        );

        for (int i = 0; i < page.size(); i++) {
            ChatFormatting style =
                    i == 0
                            ? ChatFormatting.YELLOW
                            : ChatFormatting.GRAY;

            tooltip.add(
                    Component.literal(page.get(i)).withStyle(style)
            );
        }
    }

    private static void updateFoundryTooltipPage() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft == null || minecraft.getWindow() == null) {
            return;
        }

        long window = minecraft.getWindow().getWindow();

        boolean leftDown = InputConstants.isKeyDown(
                window,
                GLFW.GLFW_KEY_LEFT
        );

        boolean rightDown = InputConstants.isKeyDown(
                window,
                GLFW.GLFW_KEY_RIGHT
        );

        if (leftDown && !wasFoundryTooltipLeftDown) {
            foundryTooltipPage = Math.floorMod(
                    foundryTooltipPage - 1,
                    FOUNDRY_TOOLTIP_PAGES.size()
            );
        }

        if (rightDown && !wasFoundryTooltipRightDown) {
            foundryTooltipPage = Math.floorMod(
                    foundryTooltipPage + 1,
                    FOUNDRY_TOOLTIP_PAGES.size()
            );
        }

        wasFoundryTooltipLeftDown = leftDown;
        wasFoundryTooltipRightDown = rightDown;
    }

    private static void addWireTooltipLines(
            List<Component> tooltip,
            EnergyWireBlock wire
    ) {
        tooltip.add(
                colored("CE: ", 0x4E8FDC)
                        .append(
                                colored(
                                        wire.tier().displayName()
                                                + " "
                                                + MachineTierStats.ceTier(
                                                wire.tier()
                                        ),
                                        0xFFFFFF
                                )
                        )
        );

        tooltip.add(
                colored("Amps: ", 0xE0A83A)
                        .append(
                                colored(
                                        Integer.toString(wire.maxAmps()),
                                        0xFFFFFF
                                )
                        )
        );

        if (wire.insulated()) {
            tooltip.add(colored("Insulated", 0xB0B0B0));
        }
    }

    private static void addCoilTooltipLines(
            List<Component> tooltip,
            CoilBlock coil
    ) {
        tooltip.add(
                colored("Heat: ", 0xFFD800)
                        .append(
                                colored(
                                        coil.definition().heat() + " C",
                                        0xFF5533
                                )
                        )
        );
    }

    public static void addMaterialTooltipLines(
            List<Component> tooltip,
            MaterialLookup.MaterialTarget target
    ) {
        IndustrialMaterial material = target.material();

        Component formula = formulaLine(material);
        if (formula != null) {
            tooltip.add(formula);
        }

        tooltip.add(
                colored("State: ", 0xFF66CC)
                        .append(
                                colored(
                                        materialState(
                                                material,
                                                target.part()
                                        ),
                                        0xFF66CC
                                )
                        )
        );

        if (material.hasExplicitStrength()) {
            tooltip.add(
                    colored("Strength: ", 0x2ECC40)
                            .append(
                                    colored(
                                            Integer.toString(
                                                    material.strength()
                                            ),
                                            0xFFFFFF
                                    )
                            )
            );
        }

        if (showsMeltingPoint(material)) {
            tooltip.add(
                    colored("Melting Point: ", 0xFFD800)
                            .append(
                                    colored(
                                            material.meltingPoint() + " C",
                                            0xFF3333
                                    )
                            )
            );
        }

        if (material.radioactivity() > 0) {
            tooltip.add(
                    colored("Radioactivity: ", 0xBFFF00)
                            .append(
                                    colored(
                                            Integer.toString(
                                                    material.radioactivity()
                                            ),
                                            0xBFFF00
                                    )
                            )
            );
        }

        if (showsTemperature(target.part())) {
            tooltip.add(
                    colored("Temperature: ", 0xFF3333)
                            .append(
                                    colored(
                                            material.temperatureFor(
                                                    target.part()
                                            ) + " C",
                                            0xFF3333
                                    )
                            )
            );
        }
    }

    private static Component formulaLine(
            IndustrialMaterial material
    ) {
        MutableComponent formula =
                formulaComponent(material, false);

        if (formula == null) {
            return null;
        }

        return Component.literal("Formula: ")
                .withStyle(ChatFormatting.BLUE)
                .append(formula);
    }

    private static MutableComponent formulaComponent(
            IndustrialSubstance substance,
            boolean nested
    ) {
        if (substance instanceof IndustrialMaterial material) {
            if (material.elementSymbol().isPresent()) {
                return colored(
                        material.elementSymbol().get(),
                        material.color()
                );
            }

            return compoundFormulaComponent(
                    material,
                    material.components(),
                    nested
            );
        }

        if (substance instanceof IndustrialFluid fluid) {
            return compoundFormulaComponent(
                    fluid,
                    fluid.components(),
                    nested
            );
        }

        String formula = substance.formula();

        return formula.isBlank()
                ? null
                : colored(formula, substance.color());
    }

    private static MutableComponent compoundFormulaComponent(
            IndustrialSubstance substance,
            List<MaterialComponent> components,
            boolean nested
    ) {
        if (components.isEmpty()) {
            String formula = substance.formula();

            return formula.isBlank()
                    ? null
                    : colored(formula, substance.color());
        }

        MutableComponent result = Component.empty();

        if (nested) {
            result.append(colored("(", substance.color()));
        }

        for (MaterialComponent component : components) {
            IndustrialSubstance componentSubstance =
                    component.substance();

            boolean compoundComponent =
                    hasNestedComponents(componentSubstance);

            MutableComponent componentFormula =
                    formulaComponent(
                            componentSubstance,
                            compoundComponent
                    );

            if (componentFormula == null) {
                componentFormula = colored(
                        componentSubstance.displayName(),
                        componentSubstance.color()
                );
            }

            result.append(componentFormula);

            if (component.amount() > 1) {
                result.append(
                        colored(
                                toSubscript(component.amount()),
                                componentSubstance.color()
                        )
                );
            }
        }

        if (nested) {
            result.append(colored(")", substance.color()));
        }

        return result;
    }

    private static boolean hasNestedComponents(
            IndustrialSubstance substance
    ) {
        if (substance instanceof IndustrialMaterial material) {
            return material.elementSymbol().isEmpty()
                    && !material.components().isEmpty();
        }

        if (substance instanceof IndustrialFluid fluid) {
            return !fluid.components().isEmpty();
        }

        return false;
    }

    private static String materialState(
            IndustrialMaterial material,
            MaterialPart part
    ) {
        if (part == MaterialPart.MOLTEN_FLUID) {
            return isGasAtRoomTemperature(material)
                    ? "Gas"
                    : "Fluid";
        }

        return isFluidAtRoomTemperature(material)
                ? "Fluid"
                : "Solid";
    }

    private static boolean isFluidAtRoomTemperature(
            IndustrialMaterial material
    ) {
        return material.meltingPoint() <= 20
                || isGasAtRoomTemperature(material);
    }

    private static boolean isGasAtRoomTemperature(
            IndustrialMaterial material
    ) {
        return switch (material.id()) {
            case "hydrogen",
                 "helium",
                 "nitrogen",
                 "oxygen",
                 "fluorine",
                 "neon",
                 "chlorine",
                 "argon",
                 "krypton",
                 "xenon",
                 "radon",
                 "oganesson" -> true;

            default -> false;
        };
    }

    private static boolean showsTemperature(MaterialPart part) {
        return part == MaterialPart.MOLTEN_FLUID
                || (
                part.name().startsWith("CAST_")
                        && !part.name().endsWith("_MOLD")
        )
                || part.name().startsWith("HOT_CAST_");
    }

    private static boolean showsMeltingPoint(
            IndustrialMaterial material
    ) {
        return material.hasExplicitMeltingPoint()
                || !material.components().isEmpty();
    }

    private static MutableComponent colored(
            String text,
            int color
    ) {
        return Component.literal(text)
                .withStyle(style ->
                        style.withColor(
                                TextColor.fromRgb(color)
                        )
                );
    }

    private static String toSubscript(int number) {
        String value = Integer.toString(number);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            result.append(
                    switch (value.charAt(i)) {
                        case '0' -> '\u2080';
                        case '1' -> '\u2081';
                        case '2' -> '\u2082';
                        case '3' -> '\u2083';
                        case '4' -> '\u2084';
                        case '5' -> '\u2085';
                        case '6' -> '\u2086';
                        case '7' -> '\u2087';
                        case '8' -> '\u2088';
                        case '9' -> '\u2089';
                        default -> value.charAt(i);
                    }
            );
        }

        return result.toString();
    }
}