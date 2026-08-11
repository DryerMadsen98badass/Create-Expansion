package net.mads.createexpansion.material.recipes;

import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.MaterialLookup;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.EnumMap;
import java.util.Map;

public final class FoundryCastingRecipes {
    private static final int MB_PER_INGOT = 144;
    private static final int TICKS_PER_INGOT = 200;
    private static final Map<MaterialPart, CastShape> SHAPES = new EnumMap<>(MaterialPart.class);

    static {
        shape(MaterialPart.CAST_NUGGET_MOLD, MaterialPart.HOT_CAST_NUGGET_MOLD, MaterialPart.CAST_NUGGET, MaterialPart.NUGGET, 16);
        shape(MaterialPart.CAST_INGOT_MOLD, MaterialPart.HOT_CAST_INGOT_MOLD, MaterialPart.CAST_INGOT, MaterialPart.INGOT, 144);
        shape(MaterialPart.CAST_PLATE_MOLD, MaterialPart.HOT_CAST_PLATE_MOLD, MaterialPart.CAST_PLATE, MaterialPart.PLATE, 144);
        shape(MaterialPart.CAST_ROD_MOLD, MaterialPart.HOT_CAST_ROD_MOLD, MaterialPart.CAST_ROD, MaterialPart.ROD, 72);
        shape(MaterialPart.CAST_LONG_ROD_MOLD, MaterialPart.HOT_CAST_LONG_ROD_MOLD, MaterialPart.CAST_LONG_ROD, MaterialPart.LONG_ROD, 144);
        shape(MaterialPart.CAST_BOLT_MOLD, MaterialPart.HOT_CAST_BOLT_MOLD, MaterialPart.CAST_BOLT, MaterialPart.BOLT, 36);
        shape(MaterialPart.CAST_SCREW_MOLD, MaterialPart.HOT_CAST_SCREW_MOLD, MaterialPart.CAST_SCREW, MaterialPart.SCREW, 36);
        shape(MaterialPart.CAST_RING_MOLD, MaterialPart.HOT_CAST_RING_MOLD, MaterialPart.CAST_RING, MaterialPart.RING, 72);
        shape(MaterialPart.CAST_SMALL_RING_MOLD, MaterialPart.HOT_CAST_SMALL_RING_MOLD, MaterialPart.CAST_SMALL_RING, MaterialPart.SMALL_RING, 36);
        shape(MaterialPart.CAST_LARGE_RING_MOLD, MaterialPart.HOT_CAST_LARGE_RING_MOLD, MaterialPart.CAST_LARGE_RING, MaterialPart.LARGE_RING, 144);
        shape(MaterialPart.CAST_GEAR_MOLD, MaterialPart.HOT_CAST_GEAR_MOLD, MaterialPart.CAST_GEAR, MaterialPart.GEAR, 576);
        shape(MaterialPart.CAST_SMALL_GEAR_MOLD, MaterialPart.HOT_CAST_SMALL_GEAR_MOLD, MaterialPart.CAST_SMALL_GEAR, MaterialPart.SMALL_GEAR, 144);
        shape(MaterialPart.CAST_BEARING_BALL_MOLD, MaterialPart.HOT_CAST_BEARING_BALL_MOLD, MaterialPart.CAST_BEARING_BALL, MaterialPart.BEARING_BALL, 36);
//        shape(MaterialPart.CAST_BEARING_MOLD, MaterialPart.HOT_CAST_BEARING_MOLD, MaterialPart.CAST_BEARING, MaterialPart.BEARING, 288);
        shape(MaterialPart.CAST_ROTOR_MOLD, MaterialPart.HOT_CAST_ROTOR_MOLD, MaterialPart.CAST_ROTOR, MaterialPart.ROTOR, 576);
    }

    private FoundryCastingRecipes() {
    }

    private static void shape(MaterialPart mold, MaterialPart hotMold, MaterialPart cast, MaterialPart cooled, int amountMb) {
        SHAPES.put(mold, new CastShape(mold, hotMold, cast, cooled, amountMb, durationTicks(amountMb)));
    }

    public static CastRecipe recipe(ItemStack mold, FluidStack fluid) {
        MaterialLookup.MaterialTarget moldTarget = MaterialLookup.find(mold);
        MaterialLookup.MaterialTarget fluidTarget = MaterialLookup.find(fluid);
        if (moldTarget == null || fluidTarget == null || fluid.isEmpty()) {
            return null;
        }

        CastShape shape = SHAPES.get(moldTarget.part());
        if (shape == null || fluid.getAmount() < shape.amountMb()) {
            return null;
        }

        IndustrialMaterial castMaterial = fluidTarget.material();
        if (!castMaterial.has(shape.castPart())) {
            return null;
        }

        Item output = ItemRegistry.getMaterialItem(castMaterial, shape.castPart()).get();
        if (output == null) {
            return null;
        }

        return new CastRecipe(moldTarget.material(), castMaterial, shape, new ItemStack(output));
    }

    public static boolean isNormalMold(ItemStack stack) {
        MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
        return target != null && SHAPES.containsKey(target.part());
    }

    public static CastShape shapeForMold(ItemStack stack) {
        MaterialLookup.MaterialTarget target = MaterialLookup.find(stack);
        return target == null ? null : SHAPES.get(target.part());
    }

    public static ItemStack hotMoldFor(IndustrialMaterial moldMaterial, CastShape shape) {
        if (!moldMaterial.has(shape.hotMoldPart())) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(ItemRegistry.getMaterialItem(moldMaterial, shape.hotMoldPart()).get());
    }

    public static ItemStack normalMoldFor(IndustrialMaterial moldMaterial, CastShape shape) {
        if (!moldMaterial.has(shape.moldPart())) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(ItemRegistry.getMaterialItem(moldMaterial, shape.moldPart()).get());
    }

    public static Map<MaterialPart, CastShape> shapes() {
        return Map.copyOf(SHAPES);
    }

    public static int durationTicks(int amountMb) {
        return Math.max(20, Math.round(amountMb * (TICKS_PER_INGOT / (float) MB_PER_INGOT)));
    }

    public record CastShape(MaterialPart moldPart, MaterialPart hotMoldPart, MaterialPart castPart, MaterialPart cooledPart, int amountMb, int durationTicks) {
    }

    public record CastRecipe(IndustrialMaterial moldMaterial, IndustrialMaterial castMaterial, CastShape shape, ItemStack output) {
    }
}
