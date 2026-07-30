package net.mads.createexpansion;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.resources.ResourceLocation;

public final class CreateExpansionPartialModels {
    public static final PartialModel CENTRIFUGE_ROTOR = block("machines/kinetic/centrifuge/centrifuge_rotor");
    public static final PartialModel CENTRIFUGE_BASIN = block("machines/kinetic/centrifuge/centrifuge_basin");
    public static final PartialModel CENTRIFUGE_JEI_ASSEMBLY = block("machines/kinetic/centrifuge/centrifuge_jei_assembly");
    public static final PartialModel LATHE_SIDE_SHAFT = block("machines/kinetic/lathe/lathe_side_shaft");
    public static final PartialModel ROLLING_MILL_ROTOR_1 = block("machines/kinetic/rolling_mill/rolling_mill_rotor_1");
    public static final PartialModel ROLLING_MILL_ROTOR_2 = block("machines/kinetic/rolling_mill/rolling_mill_rotor_2");
    public static final PartialModel WIRE_DRAWER_SHAFT = LATHE_SIDE_SHAFT;
    public static final PartialModel HYDRAULIC_PRESS_HEAD = block("machines/kinetic/hydraulic_press/hydraulic_press_press");
    public static final PartialModel COILING_WHEEL = block("machines/kinetic/spring_coiling_machine/coiling_part_wheel");
    public static final PartialModel COILING_SPRING = block("machines/kinetic/spring_coiling_machine/coiling_part_spring");

    private CreateExpansionPartialModels() {
    }

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "block/" + path));
    }

    public static void init() {
        // Loads the static partial model fields early, before renderers ask for them.
    }
}
