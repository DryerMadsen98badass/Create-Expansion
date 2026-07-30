package net.mads.createexpansion.gui;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

/**
 * Registered progress bar textures available to CE recipe types and machines.
 *
 * <p>Most textures contain an empty frame followed by a filled frame vertically.
 * The renderer clips the filled frame according to {@link #direction()}.</p>
 */
public enum ProgressBar {
    ARC_FURNACE("arc_furnace", 20, 20),
    ARROW("arrow", 20, 20),
    ARROW_BRONZE("arrow_bronze", 20, 20),
    ARROW_MULTIPLE("arrow_multiple", 20, 20),
    ARROW_STEEL("arrow_steel", 20, 20),
    ASSEMBLER("assembler", 20, 20),
    ASSEMBLY_LINE("assembly_line", 54, 72),
    ASSEMBLY_LINE_ARROW("assembly_line_arrow", 10, 18),
    BATH("bath", 20, 20),
    BENDING("bending", 20, 20),
    BOILER_EMPTY_BRONZE("boiler_empty_bronze", 10, 27, Direction.UP),
    BOILER_EMPTY_STEEL("boiler_empty_steel", 10, 27, Direction.UP),
    BOILER_FUEL_BRONZE("boiler_fuel_bronze", 18, 18, Direction.UP),
    BOILER_FUEL_STEEL("boiler_fuel_steel", 18, 18, Direction.UP),
    BOILER_HEAT("boiler_heat", 10, 27, Direction.UP),
    CANNER("canner", 20, 20),
    CIRCUIT_ASSEMBLER("circuit_assembler", 20, 20),
    COKE_OVEN("coke_oven", 36, 18),
    COMPRESS("compress", 20, 20, Direction.DOWN),
    COMPRESS_BRONZE("compress_bronze", 20, 20, Direction.DOWN),
    COMPRESS_STEEL("compress_steel", 20, 20, Direction.DOWN),
    CRACKING("cracking", 20, 20),
    CRACKING_2("cracking_2", 21, 19),
    CRYSTALLIZATION("crystallization", 20, 20),
    DISTILLATION_TOWER("distillation_tower", 65, 75, Direction.UP),
    DISTILLATION_TOWER_BUBBLES("distillation_tower_bubbles", 11, 51, Direction.UP),
    DISTILLATION_TOWER_COIL("distillation_tower_coil", 12, 20, Direction.UP),
    EXTRACT("extract", 20, 20),
    EXTRACT_BRONZE("extract_bronze", 20, 20),
    EXTRACT_STEEL("extract_steel", 20, 20),
    EXTRUDER("extruder", 20, 20),
    FUSION("fusion", 20, 20),
    GAS_COLLECTOR("gas_collector", 20, 20),
    HAMMER("hammer", 20, 20, Direction.DOWN),
    HAMMER_BASE("hammer_base", 20, 3),
    HAMMER_BASE_BRONZE("hammer_base_bronze", 20, 3),
    HAMMER_BASE_STEEL("hammer_base_steel", 20, 3),
    HAMMER_BRONZE("hammer_bronze", 20, 20, Direction.DOWN),
    HAMMER_STEEL("hammer_steel", 20, 20, Direction.DOWN),
    LATHE("lathe", 20, 20),
    LATHE_BASE("lathe_base", 5, 9),
    MACERATE("macerate", 20, 20),
    MACERATE_BRONZE("macerate_bronze", 20, 20),
    MACERATE_STEEL("macerate_steel", 20, 20),
    MAGNET("magnet", 20, 20),
    MASS_FAB("mass_fab", 20, 20),
    MIXER("mixer", 20, 20),
    PACKER("packer", 20, 20),
    RECYCLER("recycler", 20, 20),
    REPLICATOR("replicator", 20, 20),
    RESEARCH_STATION_1("research_station_1", 27, 10, Direction.RIGHT, FrameLayout.HORIZONTAL),
    RESEARCH_STATION_2("research_station_2", 10, 18),
    RESEARCH_STATION_BASE("research_station_base", 84, 30),
    SIFT("sift", 20, 20),
    SLICE("slice", 20, 20),
    SOLAR_BRONZE("solar_bronze", 10, 10, Direction.UP),
    SOLAR_STEEL("solar_steel", 10, 10, Direction.UP),
    UNLOCK("unlock", 166, 72),
    UNPACKER("unpacker", 20, 20),
    WIREMILL("wiremill", 20, 20);

    private final ResourceLocation texture;
    private final int width;
    private final int height;
    private final Direction direction;
    private final FrameLayout frameLayout;

    ProgressBar(String name, int width, int height) {
        this(name, width, height, Direction.RIGHT);
    }

    ProgressBar(String name, int width, int height, Direction direction) {
        this(name, width, height, direction, FrameLayout.VERTICAL);
    }

    ProgressBar(String name, int width, int height, Direction direction, FrameLayout frameLayout) {
        this.texture = ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                "gui/progress_bar/progress_bar_" + name + ".png"
        );
        this.width = width;
        this.height = height;
        this.direction = direction;
        this.frameLayout = frameLayout;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Direction direction() {
        return direction;
    }

    public int filledU() {
        return frameLayout == FrameLayout.HORIZONTAL ? width : 0;
    }

    public int filledV() {
        return frameLayout == FrameLayout.VERTICAL ? height : 0;
    }

    public int textureWidth() {
        return frameLayout == FrameLayout.HORIZONTAL ? width * 2 : width;
    }

    public int textureHeight() {
        return frameLayout == FrameLayout.VERTICAL ? height * 2 : height;
    }

    public enum Direction {
        RIGHT,
        LEFT,
        UP,
        DOWN
    }

    private enum FrameLayout {
        VERTICAL,
        HORIZONTAL
    }
}
