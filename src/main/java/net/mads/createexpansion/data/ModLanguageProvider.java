package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.WireThickness;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.machine.MachineDefinition;
import net.mads.createexpansion.machine.MachinePortType;
import net.mads.createexpansion.machine.MachineTier;
import net.mads.createexpansion.machine.StaticMachinePortType;
import net.mads.createexpansion.machine.coil.CoilDefinitions;
import net.mads.createexpansion.machine.machines.electric.multiblock.MultiblockDefinitions;
import net.mads.createexpansion.machine.machines.kinetic.centrifuge.KineticCentrifugeRegistration;
import net.mads.createexpansion.machine.machines.kinetic.coiling.KineticCoilingMachineRegistration;
import net.mads.createexpansion.machine.machines.kinetic.hydraulicpress.HydraulicPressRegistration;
import net.mads.createexpansion.machine.machines.kinetic.lathe.KineticLatheRegistration;
import net.mads.createexpansion.machine.machines.kinetic.rollingmill.KineticRollingMillRegistration;
import net.mads.createexpansion.machine.machines.kinetic.sifter.KineticSifterRegistration;
import net.mads.createexpansion.machine.machines.kinetic.wiredrawer.KineticWireDrawerRegistration;
import net.mads.createexpansion.registry.FluidRegistry;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.minecraft.data.PackOutput;

public class ModLanguageProvider extends LanguageProvider {
    public ModLanguageProvider(PackOutput output) {
        super(output, CreateExpansion.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.create_expansion", "Create Expansion");
        add("itemGroup.create_expansion.industry", "Create Expansion: Industry");
        add("itemGroup.create_expansion.materials", "Create Expansion: Materials");
        SimpleItems.ALL.forEach(item ->
                add("item." + CreateExpansion.MOD_ID + "." + item.id(), item.displayName()));
        KineticSifterRegistration.addTranslations(this::add);
        KineticCentrifugeRegistration.addTranslations(this::add);
        KineticLatheRegistration.addTranslations(this::add);
        KineticRollingMillRegistration.addTranslations(this::add);
        KineticWireDrawerRegistration.addTranslations(this::add);
        HydraulicPressRegistration.addTranslations(this::add);
        KineticCoilingMachineRegistration.addTranslations(this::add);
        CoilDefinitions.ALL.forEach(coil -> {
            add("block." + CreateExpansion.MOD_ID + "." + coil.blockId(), coil.displayName());
            add("item." + CreateExpansion.MOD_ID + "." + coil.itemId(), coil.displayName());
        });
        add("block." + CreateExpansion.MOD_ID + ".foundry_casing", "Seared Bricks");
        add("block." + CreateExpansion.MOD_ID + ".foundry_controller", "Foundry Controller");
        add("block." + CreateExpansion.MOD_ID + ".foundry_input_hatch", "Seared Input Hatch");
        add("block." + CreateExpansion.MOD_ID + ".foundry_output_hatch", "Seared Output Hatch");
        add("block." + CreateExpansion.MOD_ID + ".foundry_input_bus", "Seared Input Bus");
        add("block." + CreateExpansion.MOD_ID + ".foundry_drain", "Seared Drain");
        add("block." + CreateExpansion.MOD_ID + ".foundry_mold_caster", "Mold Caster");
        MultiblockDefinitions.controllers().forEach(controller ->
                add("block." + CreateExpansion.MOD_ID + "." + controller.registryName(), controller.displayName()));
        for (MachineTier tier : MachineTier.ALL) {
            add("block." + CreateExpansion.MOD_ID + "." + tier.casingRegistryName(), tier.casingDisplayName());
            for (WireThickness thickness : WireThickness.ALL) {
                add("block." + CreateExpansion.MOD_ID + "." + EnergyWireBlock.registryName(tier, thickness, false), EnergyWireBlock.displayName(tier, thickness, false));
                add("block." + CreateExpansion.MOD_ID + "." + EnergyWireBlock.registryName(tier, thickness, true), EnergyWireBlock.displayName(tier, thickness, true));
            }
            for (MachinePortType portType : MachinePortType.ALL) {
                add("block." + CreateExpansion.MOD_ID + "." + portType.registryName(tier), portType.displayName(tier));
            }
        }

        for (StaticMachinePortType portType : StaticMachinePortType.ALL) {
            add("block." + CreateExpansion.MOD_ID + "." + portType.id(), portType.displayName());
        }

        for (MachineDefinition machine : MachineDefinition.ALL) {
            add("block." + CreateExpansion.MOD_ID + "." + machine.controllerRegistryName(), machine.tier().displayName() + " " + machine.displayName());
        }

        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (part.isFluid()) {
                    continue;
                }

                String type = part.isBlock() ? "block" : "item";
                add(type + "." + CreateExpansion.MOD_ID + "." + part.registryName(material), part.readableName(material));
            }
        }

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.allFluids()) {
            add("fluid_type." + CreateExpansion.MOD_ID + "." + fluid.definition().registryName(), fluid.definition().localizedName());
            add("fluid." + CreateExpansion.MOD_ID + "." + fluid.definition().registryName(), fluid.definition().localizedName());
            add("item." + CreateExpansion.MOD_ID + "." + fluid.definition().bucketName(), fluid.definition().bucketDisplayName());
        }
    }
}
