package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.block.SimpleBlocks;
import net.mads.createexpansion.block.SimpleBlockDefinition;
import net.mads.createexpansion.block.SimpleBlockVariant;
import net.mads.createexpansion.energy.EnergyWireBlock;
import net.mads.createexpansion.energy.WireThickness;
import net.mads.createexpansion.item.SimpleItems;
import net.mads.createexpansion.machine.MachineDefinition;
import net.mads.createexpansion.machine.SingleBlockMachineInstance;
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
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.FluidRegistry;
import net.mads.createexpansion.transport.FluidTransportTier;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLanguageProvider extends LanguageProvider {

    public ModLanguageProvider(PackOutput output) {
        super(
                output,
                CreateExpansion.MOD_ID,
                "en_us"
        );
    }

    @Override
    protected void addTranslations() {
        add(
                "itemGroup.create_expansion",
                "Create Expansion"
        );

        add(
                "itemGroup.create_expansion.industry",
                "Create Expansion: Industry"
        );

        add(
                "itemGroup.create_expansion.materials",
                "Create Expansion: Materials"
        );

        add(itemKey("machine_control_schedule"), "Machine Control Schedule");
        add("gui.create_expansion.machine_control_schedule", "Machine Control Schedule");

        addSimpleItems();
        addFiredBuckets();
        addSimpleBlocks();
        addActiveBlocks();
        addFluidTransport();
        addJadeConfigTranslations();

        KineticSifterRegistration.addTranslations(this::add);
        KineticCentrifugeRegistration.addTranslations(this::add);
        KineticLatheRegistration.addTranslations(this::add);
        KineticRollingMillRegistration.addTranslations(this::add);
        KineticWireDrawerRegistration.addTranslations(this::add);
        HydraulicPressRegistration.addTranslations(this::add);
        KineticCoilingMachineRegistration.addTranslations(this::add);

        addCoils();
        addFoundryBlocks();
        addMultiblockControllers();
        addMachineBlocks();
        addMaterials();
        addFluids();
    }

    private void addSimpleItems() {
        SimpleItems.ALL.forEach(definition ->
                add(
                        itemKey(definition.id()),
                        definition.displayName()
                )
        );
    }

    private void addSimpleBlocks() {
        for (SimpleBlockDefinition definition : SimpleBlocks.ALL) {
            add(
                    blockKey(definition.id()),
                    definition.displayName()
            );

            for (SimpleBlockVariant variant
                    : definition.variants()) {

                add(
                        blockKey(
                                definition.variantId(variant)
                        ),
                        definition.variantDisplayName(variant)
                );
            }
        }
    }

    private void addActiveBlocks() {
        SimpleBlocks.ACTIVE.forEach(definition ->
                add(
                        blockKey(definition.id()),
                        definition.displayName()
                )
        );
    }

    private void addFiredBuckets() {
        add(itemKey("fired_bucket"), "Fired Bucket");
        add(itemKey("fired_water_bucket"), "Fired Water Bucket");
        add(itemKey("fired_lava_bucket"), "Fired Lava Bucket");

        for (FluidRegistry.RegisteredFluid fluid : FluidRegistry.allFluids()) {
            add(
                    itemKey(fluid.firedBucket().getId().getPath()),
                    "Fired " + fluid.definition().bucketDisplayName()
            );
        }
    }

    private void addFluidTransport() {
        for (FluidTransportTier tier : FluidTransportTier.all()) {
            add(blockKey(tier.pipeId()), tier.pipeDisplayName());
            add(blockKey(tier.glassPipeId()), tier.glassPipeDisplayName());
            add(blockKey(tier.pumpId()), tier.pumpDisplayName());
            add(blockKey(tier.tankId()), tier.tankDisplayName());
        }
    }

    private void addJadeConfigTranslations() {
        add("config.jade.plugin_create_expansion.ce_energy_storage", "CE Energy Storage");
        add("config.jade.plugin_create_expansion.assembly_block", "Assembly Block");
        add("config.jade.plugin_create_expansion.machine_info", "Machine Information");
        add("config.jade.plugin_create_expansion.multiblock_status", "Multiblock Status");
        add("config.jade.plugin_create_expansion.ce_wire", "CE Wire");
    }

    private void addCoils() {
        CoilDefinitions.ALL.forEach(coil -> {
            add(
                    blockKey(coil.blockId()),
                    coil.displayName()
            );

            add(
                    itemKey(coil.itemId()),
                    coil.displayName()
            );
        });
    }

    private void addFoundryBlocks() {
        add(
                blockKey("foundry_casing"),
                "Seared Bricks"
        );

        add(
                blockKey("foundry_controller"),
                "Foundry Controller"
        );

        add(
                blockKey("creative_foundry_controller"),
                "Creative Foundry Controller"
        );

        add(
                blockKey("foundry_input_hatch"),
                "Seared Input Hatch"
        );

        add(
                blockKey("foundry_output_hatch"),
                "Seared Output Hatch"
        );

        add(
                blockKey("foundry_input_bus"),
                "Seared Input Bus"
        );

        add(
                blockKey("foundry_drain"),
                "Seared Drain"
        );

        add(
                blockKey("foundry_mold_caster"),
                "Mold Caster"
        );
    }

    private void addMultiblockControllers() {
        MultiblockDefinitions.controllers().forEach(controller ->
                add(
                        blockKey(
                                controller.registryName()
                        ),
                        controller.displayName()
                )
        );
    }

    private void addMachineBlocks() {
        for (MachineTier tier : MachineTier.ALL) {
            add(
                    blockKey(
                            tier.casingRegistryName()
                    ),
                    tier.casingDisplayName()
            );

            for (WireThickness thickness
                    : WireThickness.ALL) {

                add(
                        blockKey(
                                EnergyWireBlock.registryName(
                                        tier,
                                        thickness,
                                        false
                                )
                        ),
                        EnergyWireBlock.displayName(
                                tier,
                                thickness,
                                false
                        )
                );

                add(
                        blockKey(
                                EnergyWireBlock.registryName(
                                        tier,
                                        thickness,
                                        true
                                )
                        ),
                        EnergyWireBlock.displayName(
                                tier,
                                thickness,
                                true
                        )
                );
            }

            for (MachinePortType portType
                    : MachinePortType.ALL) {

                add(
                        blockKey(
                                portType.registryName(tier)
                        ),
                        portType.displayName(tier)
                );
            }
        }

        for (StaticMachinePortType portType
                : StaticMachinePortType.ALL) {

            add(
                    blockKey(portType.id()),
                    portType.displayName()
            );
        }

        for (SingleBlockMachineInstance machine
                : MachineDefinition.INSTANCES) {

            add(
                    blockKey(machine.registryName()),
                    machine.displayName()
            );
        }
    }

    private void addMaterials() {
        for (IndustrialMaterial material
                : IndustrialMaterials.ALL) {

            for (var stoneSource
                    : material.stoneSources()) {

                if (stoneSource.isExisting()) {
                    continue;
                }

                add(
                        blockKey(
                                stoneSource.registryName(material)
                        ),
                        stoneSource.displayName(material)
                );
            }

            for (MaterialPart part : material.parts()) {
                if (material.hasExistingPart(part)) {
                    continue;
                }

                if (part.isFluid()) {
                    continue;
                }

                String translationKey =
                        part.isBlock()
                                ? blockKey(
                                part.registryName(material)
                        )
                                : itemKey(
                                part.registryName(material)
                        );

                add(
                        translationKey,
                        part.readableName(material)
                );
            }
        }
    }

    private void addFluids() {
        for (FluidRegistry.RegisteredFluid fluid
                : FluidRegistry.allFluids()) {

            String registryName =
                    fluid.definition().registryName();

            String localizedName =
                    fluid.definition().localizedName();

            add(
                    "fluid_type."
                            + CreateExpansion.MOD_ID
                            + "."
                            + registryName,
                    localizedName
            );

            add(
                    "fluid."
                            + CreateExpansion.MOD_ID
                            + "."
                            + registryName,
                    localizedName
            );

            add(
                    itemKey(
                            fluid.definition().bucketName()
                    ),
                    fluid.definition().bucketDisplayName()
            );

        }
    }

    private static String blockKey(String id) {
        return "block."
                + CreateExpansion.MOD_ID
                + "."
                + id;
    }

    private static String itemKey(String id) {
        return "item."
                + CreateExpansion.MOD_ID
                + "."
                + id;
    }
}
