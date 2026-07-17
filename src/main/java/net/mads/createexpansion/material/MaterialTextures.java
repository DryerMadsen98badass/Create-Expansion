package net.mads.createexpansion.material;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public class MaterialTextures {
    public static Optional<ResourceLocation> itemModel(IndustrialMaterial material, MaterialPart part) {
        String modelName = itemMaterialSetName(part);
        if (modelName == null) {
            return Optional.empty();
        }

        if (isCastMold(part)) {
            return Optional.of(itemMaterialSetModel("mold", modelName));
        }

        return Optional.of(itemMaterialSetModel(material.itemMaterialSet(), modelName));
    }

    public static Optional<ResourceLocation> blockModel(IndustrialMaterial material, MaterialPart part) {
        String modelName = blockMaterialSetName(part);
        if (modelName == null) {
            return Optional.empty();
        }

        return Optional.of(blockMaterialSetModel(material, modelName));
    }

    public static Optional<ResourceLocation> blockTexture(IndustrialMaterial material, MaterialPart part) {
        String textureName = blockMaterialSetName(part);
        if (textureName == null) {
            return Optional.empty();
        }

        return Optional.of(blockMaterialSetTexture(material, textureName));
    }

    public static Optional<ResourceLocation> blockOverlayTexture(IndustrialMaterial material, MaterialPart part) {
        String textureName = blockOverlayMaterialSetName(part);
        if (textureName == null) {
            return Optional.empty();
        }

        return Optional.of(blockMaterialSetTexture(material, textureName));
    }

    private static ResourceLocation itemMaterialSetModel(String materialSet, String modelName) {
        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                "item/material_sets/" + materialSet + "/" + modelName
        );
    }

    private static boolean isCastMold(MaterialPart part) {
        return switch (part) {
            case CAST_NUGGET_MOLD,
                 CAST_BEARING_BALL_MOLD,
                 CAST_ROTOR_MOLD,
                 CAST_INGOT_MOLD,
                 CAST_PLATE_MOLD,
                 CAST_ROD_MOLD,
                 CAST_LONG_ROD_MOLD,
                 CAST_BOLT_MOLD,
                 CAST_RING_MOLD,
                 CAST_SMALL_RING_MOLD,
                 CAST_LARGE_RING_MOLD,
                 CAST_GEAR_MOLD,
                 CAST_SMALL_GEAR_MOLD,
                 CAST_BEARING_MOLD,
                 CAST_SCREW_MOLD,
                 HOT_CAST_NUGGET_MOLD,
                 HOT_CAST_BEARING_BALL_MOLD,
                 HOT_CAST_ROTOR_MOLD,
                 HOT_CAST_INGOT_MOLD,
                 HOT_CAST_PLATE_MOLD,
                 HOT_CAST_ROD_MOLD,
                 HOT_CAST_LONG_ROD_MOLD,
                 HOT_CAST_BOLT_MOLD,
                 HOT_CAST_RING_MOLD,
                 HOT_CAST_SMALL_RING_MOLD,
                 HOT_CAST_LARGE_RING_MOLD,
                 HOT_CAST_GEAR_MOLD,
                 HOT_CAST_SMALL_GEAR_MOLD,
                 HOT_CAST_BEARING_MOLD,
                 HOT_CAST_SCREW_MOLD -> true;
            default -> false;
        };
    }

    private static ResourceLocation blockMaterialSetModel(IndustrialMaterial material, String modelName) {
        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                "block/material_sets/" + material.blockMaterialSet() + "/" + modelName
        );
    }

    private static ResourceLocation blockMaterialSetTexture(IndustrialMaterial material, String textureName) {
        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                "block/material_sets/" + material.blockMaterialSet() + "/" + textureName
        );
    }

    private static String itemMaterialSetName(MaterialPart part) {
        return switch (part) {
            case RAW_ORE -> "raw_ore";
            case INGOT -> "ingot";
            case NUGGET -> "nugget";
            case DUST -> "dust";
            case TINY_DUST -> "dust_tiny";
            case SMALL_DUST -> "dust_small";
            case IMPURE_DUST -> "dust_impure";
            case CRUSHED_ORE -> "crushed";
            case WASHED_CRUSHED_ORE -> "crushed_purified";
            case PURIFIED_DUST -> "dust_pure";
            case REFINED_ORE -> "crushed_refined";
            case GEM -> "gem";
            case FLAWLESS_GEM -> "gem_flawless";
            case EXQUISITE_GEM -> "gem_exquisite";
            case PLATE -> "plate";
            case DOUBLE_PLATE -> "plate_double";
            case DENSE_PLATE -> "plate_dense";
            case FOIL -> "foil";
            case ROD -> "rod";
            case LONG_ROD -> "rod_long";
            case BOLT -> "bolt";
            case SCREW -> "screw";
            case WIRE -> "wire";
            case RING -> "ring";
            case SMALL_RING -> "ring_small";
            case LARGE_RING -> "ring_large";
            case FINE_WIRE -> "wire_fine";
            case GEAR -> "gear";
            case SMALL_GEAR -> "gear_small";
            case LARGE_GEAR -> "gear_large";
            case BEARING_BALL -> "bearing_ball";
            case BEARING -> "bearing";
            case SPRING -> "spring";
            case COIL -> "coil";
            case ROTOR -> "rotor";
            case REINFORCED_PLATE -> "plate_reinforced";
            case HEAT_EXCHANGER_PLATE -> "plate_heat_exchanger";
            case CAST_INGOT -> "ingot_hot";
            case CAST_NUGGET -> "nugget";
            case CAST_BLOCK -> "block";
            case CAST_PLATE -> "plate";
            case CAST_ROD -> "rod";
            case CAST_LONG_ROD -> "rod_long";
            case CAST_BOLT -> "bolt";
            case CAST_SCREW -> "screw";
            case CAST_RING -> "ring";
            case CAST_SMALL_RING -> "ring_small";
            case CAST_LARGE_RING -> "ring_large";
            case CAST_GEAR -> "gear";
            case CAST_SMALL_GEAR -> "gear_small";
            case CAST_BEARING_BALL -> "bearing_ball";
            case CAST_BEARING -> "bearing";
            case CAST_ROTOR -> "rotor";
            case CAST_NUGGET_MOLD -> "cast_nugget_mold";
            case CAST_BEARING_BALL_MOLD -> "cast_bearing_ball_mold";
            case CAST_ROTOR_MOLD -> "cast_rotor_mold";
            case CAST_INGOT_MOLD -> "cast_ingot_mold";
            case CAST_PLATE_MOLD -> "cast_plate_mold";
            case CAST_ROD_MOLD -> "cast_rod_mold";
            case CAST_LONG_ROD_MOLD -> "cast_long_rod_mold";
            case CAST_BOLT_MOLD -> "cast_bolt_mold";
            case CAST_RING_MOLD -> "cast_ring_mold";
            case CAST_SMALL_RING_MOLD -> "cast_small_ring_mold";
            case CAST_LARGE_RING_MOLD -> "cast_large_ring_mold";
            case CAST_GEAR_MOLD -> "cast_gear_mold";
            case CAST_SMALL_GEAR_MOLD -> "cast_small_gear_mold";
            case CAST_BEARING_MOLD -> "cast_bearing_mold";
            case CAST_SCREW_MOLD -> "cast_screw_mold";
            case HOT_CAST_NUGGET_MOLD -> "cast_nugget_mold";
            case HOT_CAST_BEARING_BALL_MOLD -> "cast_bearing_ball_mold";
            case HOT_CAST_ROTOR_MOLD -> "cast_rotor_mold";
            case HOT_CAST_INGOT_MOLD -> "cast_ingot_mold";
            case HOT_CAST_PLATE_MOLD -> "cast_plate_mold";
            case HOT_CAST_ROD_MOLD -> "cast_rod_mold";
            case HOT_CAST_LONG_ROD_MOLD -> "cast_long_rod_mold";
            case HOT_CAST_BOLT_MOLD -> "cast_bolt_mold";
            case HOT_CAST_RING_MOLD -> "cast_ring_mold";
            case HOT_CAST_SMALL_RING_MOLD -> "cast_small_ring_mold";
            case HOT_CAST_LARGE_RING_MOLD -> "cast_large_ring_mold";
            case HOT_CAST_GEAR_MOLD -> "cast_gear_mold";
            case HOT_CAST_SMALL_GEAR_MOLD -> "cast_small_gear_mold";
            case HOT_CAST_BEARING_MOLD -> "cast_bearing_mold";
            case HOT_CAST_SCREW_MOLD -> "cast_screw_mold";
            default -> null;
        };
    }

    private static String blockMaterialSetName(MaterialPart part) {
        return switch (part) {
            case RAW_BLOCK -> "raw_ore_block";
            case ORE,
                 DEEPSLATE_ORE,
                 DIORITE_ORE,
                 ANDESITE_ORE,
                 GRANITE_ORE,
                 TUFF_ORE,
                 NETHERRACK_ORE,
                 BLACKSTONE_ORE,
                 END_STONE_ORE -> "ore";
            case BLOCK -> "block";
            case FRAME -> "frame_gt";
            default -> null;
        };
    }

    private static String blockOverlayMaterialSetName(MaterialPart part) {
        return switch (part) {
            case ORE,
                 DEEPSLATE_ORE,
                 DIORITE_ORE,
                 ANDESITE_ORE,
                 GRANITE_ORE,
                 TUFF_ORE,
                 NETHERRACK_ORE,
                 BLACKSTONE_ORE,
                 END_STONE_ORE -> "ore_layer2";
            default -> null;
        };
    }
}
