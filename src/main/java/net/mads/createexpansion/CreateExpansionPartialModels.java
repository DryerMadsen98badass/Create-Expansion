package net.mads.createexpansion;

import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.lang.Lang;
import net.mads.createexpansion.transport.FluidTransportTier;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private static final Map<String, PartialModel> FLUID_PIPE_CASINGS = new LinkedHashMap<>();
    private static final Map<
            String,
            Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>>
            > PIPE_ATTACHMENTS = new LinkedHashMap<>();

    static {
        for (FluidTransportTier tier : FluidTransportTier.all()) {
            FLUID_PIPE_CASINGS.put(tier.id(), block(tier.pipeId() + "/casing"));

            Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> partials =
                    new EnumMap<>(FluidTransportBehaviour.AttachmentTypes.ComponentPartials.class);
            for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial
                    : FluidTransportBehaviour.AttachmentTypes.ComponentPartials.values()) {
                Map<Direction, PartialModel> directions = new EnumMap<>(Direction.class);
                for (Direction direction : Iterate.directions) {
                    directions.put(
                            direction,
                            block(
                                    tier.pipeId()
                                            + "/"
                                            + Lang.asId(partial.name())
                                            + "/"
                                            + Lang.asId(direction.getSerializedName())
                            )
                    );
                }
                partials.put(partial, directions);
            }
            PIPE_ATTACHMENTS.put(tier.id(), partials);
        }
    }

    private CreateExpansionPartialModels() {
    }

    public static PartialModel fluidPipeCasing(FluidTransportTier tier) {
        PartialModel model = FLUID_PIPE_CASINGS.get(tier.id());
        if (model == null) {
            throw new IllegalArgumentException("Missing fluid pipe casing partial for tier " + tier.id());
        }
        return model;
    }

    public static PartialModel pipeAttachment(
            FluidTransportTier tier,
            FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial,
            Direction direction
    ) {
        Map<FluidTransportBehaviour.AttachmentTypes.ComponentPartials, Map<Direction, PartialModel>> partials =
                PIPE_ATTACHMENTS.get(tier.id());
        if (partials == null || !partials.containsKey(partial) || !partials.get(partial).containsKey(direction)) {
            throw new IllegalArgumentException("Missing fluid pipe attachment partial for tier " + tier.id());
        }
        return partials.get(partial).get(direction);
    }

    private static PartialModel block(String path) {
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, "block/" + path));
    }

    public static void init() {
        // Loads all static and per-tier partial models before renderers request them.
    }
}
