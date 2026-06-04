package net.mads.createexpansion.client;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.MaterialComponent;
import net.mads.createexpansion.material.MaterialLookup;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

@EventBusSubscriber(modid = CreateExpansion.MOD_ID, value = Dist.CLIENT)
public final class ClientMaterialTooltip {
    private ClientMaterialTooltip() {
    }

    @SubscribeEvent
    public static void addMaterialTooltip(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player == null || !GogglesItem.isWearingGoggles(player)) {
            return;
        }

        MaterialLookup.MaterialTarget target = MaterialLookup.find(event.getItemStack());
        if (target == null) {
            return;
        }

        addMaterialTooltipLines(event.getToolTip(), target);
    }

    public static void addMaterialTooltipLines(List<Component> tooltip, MaterialLookup.MaterialTarget target) {
        IndustrialMaterial material = target.material();
        Component formula = formulaLine(material);
        if (formula != null) {
            tooltip.add(formula);
        }

        tooltip.add(
                colored("State: ", 0xFF66CC)
                        .append(colored(materialState(material, target.part()), 0xFF66CC))
        );
        if (material.hasExplicitStrength()) {
            tooltip.add(
                    colored("Strength: ", 0x2ECC40)
                            .append(colored(Integer.toString(material.strength()), 0xFFFFFF))
            );
        }

        if (material.hasExplicitMeltingPoint()) {
            tooltip.add(
                    colored("Melting Point: ", 0xFFD800)
                            .append(colored(material.meltingPoint() + " C", 0xFF3333))
            );
        }
        if (material.radioactivity() > 0) {
            tooltip.add(
                    colored("Radioactivity: ", 0xBFFF00)
                            .append(colored(Integer.toString(material.radioactivity()), 0xBFFF00))
            );
        }

        if (showsTemperature(target.part())) {
            tooltip.add(
                    colored("Temperature: ", 0xFF3333)
                            .append(colored(material.temperatureFor(target.part()) + " C", 0xFF3333))
            );
        }
    }

    private static Component formulaLine(IndustrialMaterial material) {
        MutableComponent formula = formulaComponent(material, false);
        if (formula == null) {
            return null;
        }

        return Component.literal("Formula: ")
                .withStyle(ChatFormatting.BLUE)
                .append(formula);
    }

    private static MutableComponent formulaComponent(IndustrialMaterial material, boolean nested) {
        if (material.elementSymbol().isPresent()) {
            return colored(material.elementSymbol().get(), material.color());
        }

        if (material.components().isEmpty()) {
            String formula = material.formula();
            return formula.isBlank() ? null : colored(formula, material.color());
        }

        MutableComponent result = Component.empty();
        if (nested) {
            result.append(colored("(", material.color()));
        }

        for (MaterialComponent component : material.components()) {
            boolean compoundComponent = component.material().elementSymbol().isEmpty() && !component.material().components().isEmpty();
            MutableComponent componentFormula = formulaComponent(component.material(), compoundComponent);
            if (componentFormula == null) {
                componentFormula = colored(component.material().displayName(), component.material().color());
            }

            result.append(componentFormula);
            if (component.amount() > 1) {
                result.append(colored(toSubscript(component.amount()), component.material().color()));
            }
        }

        if (nested) {
            result.append(colored(")", material.color()));
        }

        return result;
    }

    private static String materialState(IndustrialMaterial material, MaterialPart part) {
        if (part == MaterialPart.MOLTEN_FLUID) {
            return isGasAtRoomTemperature(material) ? "Gas" : "Fluid";
        }

        return isFluidAtRoomTemperature(material) ? "Fluid" : "Solid";
    }

    private static boolean isFluidAtRoomTemperature(IndustrialMaterial material) {
        return material.meltingPoint() <= 20 || isGasAtRoomTemperature(material);
    }

    private static boolean isGasAtRoomTemperature(IndustrialMaterial material) {
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
                || part.name().startsWith("CAST_")
                || part.name().startsWith("HOT_CAST_");
    }

    private static MutableComponent colored(String text, int color) {
        return Component.literal(text).withStyle(style -> style.withColor(TextColor.fromRgb(color)));
    }

    private static String toSubscript(int number) {
        String value = Integer.toString(number);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            result.append(switch (value.charAt(i)) {
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
            });
        }

        return result.toString();
    }
}
