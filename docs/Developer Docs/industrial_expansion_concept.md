# Create Expansion: Industrial Expansion Concept

Create Expansion is a large-scale addon for the Minecraft mod Create. The goal is to extend Create's mechanical progression into a deeper industrial ecosystem with materials, foundries, logistics, fluids, electricity, chemistry, and eventually large multiblock machines.

The mod should not replace Create's mechanical identity. It should make Create's systems stay useful for longer by adding new problems to solve.

## Core Principle

Progression introduces new challenges, not just better numbers.

Higher tiers should not simply be faster or stronger. They should add new constraints such as heat, pressure, cooling, voltage, fluid routing, batching, timing, efficiency, and safety.

## Design Philosophy

- Mechanical systems remain relevant throughout the entire game.
- New tiers add new engineering problems.
- Automation is earned through industrial problem-solving.
- Efficiency matters more than raw speed.
- Old systems are not made obsolete. They become part of larger systems.
- Electricity supports kinetic systems instead of replacing them.
- Scaling should be non-linear, so overclocking and speed are not always the best answer.

## Current Project State

The project currently has the foundation for:

- Universal material definitions.
- Material parts such as ingots, nuggets, plates, rods, gears, dusts, frames, cast parts, and molds.
- Existing-item support for vanilla/Create materials such as iron, copper, gold, zinc, brass, andesite alloy, redstone, diamond, emerald, lapis, quartz, and others.
- Molten fluids for materials.
- Chemical/fluid definitions.
- Material formulas from elements and components.
- Tooltips for formula, strength, melting point, temperature, and radioactivity.
- Radioactive material damage.
- Custom ore vein world generation.
- Surface indicators above ore veins.
- A command for locating ore veins.

Ore processing recipes are intentionally not the next focus. Ore processing should be designed after the relevant machines exist.

## Materials

Materials are defined once and can then receive groups of parts.

Example style:

```java
material("iron", "Iron", 0xD8D8D8)
        .element("Fe")
        .allMetals()
        .allMaterials()
        .strength(6)
        .meltingPoint(1538)
        .build();
```

Alloys and minerals can be built from components.

Example:

```java
material("bronze", "Bronze", 0xCD7F32)
        .contains(component(COPPER, 3), component(TIN, 1))
        .allMetals()
        .allMaterials()
        .build();
```

If a material is made from other materials, its formula can be calculated automatically.

## Material Part Groups

### All Metals

Metal ore materials usually receive:

- Ore
- Deepslate Ore
- Diorite Ore
- Andesite Ore
- Granite Ore
- Tuff Ore
- Netherrack Ore
- Blackstone Ore
- End Stone Ore
- Raw Ore
- Raw Block
- Crushed Ore
- Washed Crushed Ore
- Refined Ore
- Impure Dust
- Purified Dust
- Dust
- Small Dust
- Tiny Dust
- Ingot
- Nugget
- Block
- Frame

### All Gems

Gem materials usually receive:

- Ore
- Deepslate Ore
- Diorite Ore
- Andesite Ore
- Granite Ore
- Tuff Ore
- Netherrack Ore
- Blackstone Ore
- End Stone Ore
- Raw Ore
- Raw Block
- Crushed Ore
- Washed Crushed Ore
- Refined Ore
- Impure Dust
- Purified Dust
- Dust
- Small Dust
- Tiny Dust
- Gem
- Flawless Gem
- Exquisite Gem
- Block

### All Materials

General industrial materials usually receive:

- Ingot
- Nugget
- Block
- Dust
- Tiny Dust
- Plate
- Double Plate
- Foil
- Rod
- Long Rod
- Bolt
- Screw
- Wire
- Fine Wire
- Ring
- Small Ring
- Large Ring
- Gear
- Small Gear
- Large Gear
- Bearing Ball
- Bearing
- Spring
- Coil
- Rotor
- Frame
- Molten Fluid
- Cast Ingot
- Cast Nugget
- Cast Block
- Cast Plate
- Cast Rod
- Cast Long Rod
- Cast Bolt
- Cast Screw
- Cast Ring
- Cast Small Ring
- Cast Large Ring
- Cast Gear
- Cast Small Gear
- Cast Bearing Ball
- Cast Bearing
- Cast Rotor
- Cast molds
- Hot cast molds
- Reinforced Plate
- Dense Plate
- Heat Exchanger Plate

The following older ideas are currently not part of the active design:

- Sheet
- Powder
- Impeller
- Concentrate
- Structural Beam
- Industrial Frame
- Heat Resistant Plate
- Pressure Plate

Pressure Plate was replaced by Dense Plate.

## Foundry And Casting

Foundry gameplay is planned as an early industrial system.

Players should:

- Heat metals into molten fluids.
- Use molds to shape molten metal.
- Manage heat and cooling time.
- Receive hot cast molds after molten metal is poured.
- Cool hot cast molds into cast molds or cast parts.

Temperature rules:

- Molten material temperature should use the material melting point.
- Cast parts and hot cast molds should use 50 percent of the material melting point, rounded to an integer.
- Normal solid items do not need meaningful temperature yet unless a recipe or machine needs it.

Casting should feel like a process, not just a crafting shortcut.

## Fluids And Gases

The system supports material fluids and chemical fluids.

Rules:

- Metals should use molten fluid textures.
- Liquids should use liquid textures.
- Gases should use gas textures.
- Fluids should not be placeable directly in the world.
- Buckets should exist for handling fluids.
- Gas buckets should visually be upside down.
- Fluids need both bucket visuals and raw fluid visuals for machines and recipe displays.

## Ore Vein World Generation

Ore generation is based on large veins instead of small random vanilla ore clusters.

Current rules:

- The world is divided into 9x9 chunk grids.
- Each grid can choose one ore deposit.
- A deposit is about 3x3 chunks wide.
- Deposits are biome-dependent.
- Deposits use weights as rarity and selection chance.
- Normal vanilla ore generation is removed.
- Create zinc ore generation is removed.
- Village areas can get a bonus vein of coal, copper, iron, tin, or zinc.

Ore blocks can generate in:

- Stone
- Deepslate
- Diorite
- Andesite
- Granite
- Tuff
- Netherrack
- Blackstone
- End Stone

The ore vein locator command is:

```mcfunction
/create_expansion ore_vein iron
/create_expansion ore_vein copper
/create_expansion ore_vein diamond
/create_expansion ore_vein end_crystal
```

The command can search by deposit name or by material/layer name.

## Surface Indicators

Ore veins can create small natural signs on the surface. These should hint that something is nearby without revealing the exact ore.

Current indicator types:

- Lava Pool
- Stone Spot
- Dead Soil
- Gravel Patch
- Cracked Ground
- Crystal Spot
- Dead Plants
- Boulder Cluster

Crystal Spot should only be used for rare gem deposits such as diamond, emerald, topaz, ruby, sapphire, and rare End crystal deposits.

Village bonus veins do not currently create surface indicators, to avoid damaging villages.

## Progression Tiers

### Early Industrial

Main themes:

- Bronze
- Iron
- Steam
- Basic foundry systems
- Simple molds
- Early kinetic machines

Challenges:

- Heat control
- Basic logistics
- Slow but efficient machines

### Industrial Expansion

Main themes:

- Steel
- Oil
- Better alloys
- Larger factories

Challenges:

- Throughput
- Fluids
- Lubrication
- Batch processing

### Electrical Age

Main themes:

- Electricity
- Motors
- Alternators/generators
- Electric furnaces
- Electric precision machines

Challenges:

- Voltage
- Distribution
- Conversion between electricity and kinetic rotation

### Chemical Processing Age

Main themes:

- Acids
- Gases
- Industrial fluids
- Multi-step chemical chains

Challenges:

- Fluid routing
- Gas routing
- Safety
- Multi-stage recipes

### Nuclear Age

Main themes:

- Uranium
- Thorium
- Reactor heat
- Cooling systems
- Steam turbines

Challenges:

- Heat balancing
- Cooling
- Radiation
- High-output energy generation

## Small Machines

Small machines should be slower but efficient. They are important because they stay useful inside larger systems.

Planned examples:

- Crusher
- Press
- Mixer
- Centrifuge
- Lathe
- Cutter
- Laser Engraver
- Drill
- Deployer
- Extractor
- Steam Engine
- Combustion Engine
- Gas Turbine
- Steam Turbine
- Motor
- Alternator / Generator
- Electric Furnace
- Electric Blast Furnace
- Oil Pump
- Compressor
- Gas Separator
- Ore Washer
- Electrolyzer

The next good development step is to create a reusable base system for small machines, then implement the first simple machines.

## Multiblock Industrial Machines

Multiblocks are planned as large-scale systems focused on logistics and throughput.

Examples:

- Industrial Crusher
- Industrial Press
- Industrial Mixer
- Industrial Centrifuge
- Chemical Plant
- Distillation Tower
- Cracking Unit
- Refinery System
- Vacuum Chamber
- Ore Processing Plant
- Electrolysis Plant
- Assembly Machine
- Precision Assembler
- Recycling Plant
- Industrial Steam Turbine Plant
- Gas Power Plant
- Nuclear Reactor Core
- Nuclear Cooling System
- Energy Storage Facility
- High Voltage Power Station

They should use structured inputs:

- Item buses
- Fluid hatches
- Gas hatches
- Kinetic input
- Electrical input

The goal is for multiblocks to be logistical puzzles, not simple upgrades.

## Scaling And Efficiency

Machines should have optimal operating ranges.

Rules to explore later:

- Higher speed should not always mean higher efficiency.
- Overclocking can increase SU cost.
- Overclocking can reduce efficiency.
- Some machines should prefer steady input instead of bursts.
- Some machines should work in cycles: fill, process, stop, extract, repeat.

This makes factory design about engineering decisions, not only raw power.

## Recommended Next Steps

1. Test and balance ore veins.
2. Test surface indicators in new chunks.
3. Clean up material/tooltips if needed.
4. Design a reusable small machine base.
5. Implement the first small machine.
6. Add foundry/casting gameplay.
7. Add ore processing only after the required machines exist.
