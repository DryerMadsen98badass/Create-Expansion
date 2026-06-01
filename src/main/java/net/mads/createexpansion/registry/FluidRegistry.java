package net.mads.createexpansion.registry;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.fluid.IndustrialFluid;
import net.mads.createexpansion.fluid.IndustrialFluids;
import net.mads.createexpansion.fluid.MaterialFluidType;
import net.mads.createexpansion.fluid.NonPlaceableBucketItem;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.concurrent.atomic.AtomicReference;

public class FluidRegistry {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, CreateExpansion.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, CreateExpansion.MOD_ID);
    public static final DeferredRegister<Item> BUCKET_ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, CreateExpansion.MOD_ID);

    public static final Map<String, RegisteredFluid> MATERIAL_FLUIDS = new LinkedHashMap<>();
    public static final Map<String, RegisteredFluid> CHEMICAL_FLUIDS = new LinkedHashMap<>();

    static {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            if (material.has(MaterialPart.MOLTEN_FLUID) && !material.hasExistingPart(MaterialPart.MOLTEN_FLUID)) {
                IndustrialFluid fluid = materialFluid(material);
                MATERIAL_FLUIDS.put(fluid.registryName(), register(fluid));
            }
        }

        for (IndustrialFluid fluid : IndustrialFluids.ALL) {
            CHEMICAL_FLUIDS.put(fluid.registryName(), register(fluid));
        }
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        BUCKET_ITEMS.register(modEventBus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (RegisteredFluid fluid : allFluids()) {
            event.registerItem(
                    Capabilities.FluidHandler.ITEM,
                    (stack, context) -> new FluidBucketWrapper(stack),
                    fluid.bucket().get()
            );
        }
    }

    public static Collection<RegisteredFluid> allFluids() {
        return java.util.stream.Stream.concat(MATERIAL_FLUIDS.values().stream(), CHEMICAL_FLUIDS.values().stream()).toList();
    }

    public static Collection<DeferredHolder<Item, ? extends Item>> getAllBucketItems() {
        Collection<DeferredHolder<Item, ? extends Item>> buckets = new ArrayList<>();
        for (RegisteredFluid fluid : allFluids()) {
            buckets.add(fluid.bucket());
        }

        return buckets;
    }

    private static RegisteredFluid register(IndustrialFluid fluid) {
        DeferredHolder<FluidType, MaterialFluidType> type = FLUID_TYPES.register(fluid.registryName(), () -> new MaterialFluidType(fluid));
        AtomicReference<DeferredHolder<Fluid, BaseFlowingFluid.Source>> source = new AtomicReference<>();
        AtomicReference<DeferredHolder<Fluid, BaseFlowingFluid.Flowing>> flowing = new AtomicReference<>();
        AtomicReference<DeferredHolder<Item, NonPlaceableBucketItem>> bucket = new AtomicReference<>();

        Supplier<BaseFlowingFluid.Properties> properties = () -> new BaseFlowingFluid.Properties(type, () -> source.get().get(), () -> flowing.get().get())
                .bucket(() -> bucket.get().get())
                .tickRate(fluid.isGas() ? 2 : 5)
                .levelDecreasePerBlock(fluid.isGas() ? 2 : 1);

        source.set(FLUIDS.register(fluid.registryName(), () -> new BaseFlowingFluid.Source(properties.get())));
        flowing.set(FLUIDS.register("flowing_" + fluid.registryName(), () -> new BaseFlowingFluid.Flowing(properties.get())));
        bucket.set(BUCKET_ITEMS.register(fluid.bucketName(), () ->
                new NonPlaceableBucketItem(source.get().get(), new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1))));

        return new RegisteredFluid(fluid, type, source.get(), flowing.get(), bucket.get());
    }

    private static IndustrialFluid materialFluid(IndustrialMaterial material) {
        if (isGasAtRoomTemperature(material)) {
            return IndustrialFluids.gas(material.id(), material.displayName(), material.color())
                    .temperature(300)
                    .build();
        }

        if (material.meltingPoint() <= 20) {
            return IndustrialFluids.fluid(material.id(), material.displayName(), material.color())
                    .temperature(300)
                    .build();
        }

        return IndustrialFluids.molten(material.id(), material.displayName(), material.color(), material.meltingPoint()).build();
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

    public record RegisteredFluid(
            IndustrialFluid definition,
            DeferredHolder<FluidType, MaterialFluidType> type,
            DeferredHolder<Fluid, BaseFlowingFluid.Source> source,
            DeferredHolder<Fluid, BaseFlowingFluid.Flowing> flowing,
            DeferredHolder<Item, NonPlaceableBucketItem> bucket
    ) {
    }
}
