package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FoundryLootProvider implements DataProvider {

    private static final List<String> BLOCKS = List.of(
            "foundry_casing",
            "foundry_controller",
            "creative_foundry_controller",
            "foundry_input_hatch",
            "foundry_output_hatch",
            "foundry_input_bus",
            "foundry_drain",
            "foundry_mold_caster"
    );

    private final PackOutput.PathProvider lootTables;

    public FoundryLootProvider(PackOutput output) {
        this.lootTables = output.createPathProvider(
                PackOutput.Target.DATA_PACK,
                "loot_table/blocks"
        );
    }

    @Override
    public CompletableFuture<?> run(
            CachedOutput output
    ) {
        List<CompletableFuture<?>> futures =
                new ArrayList<>();

        for (String blockId : BLOCKS) {
            ResourceLocation block =
                    ResourceLocation.fromNamespaceAndPath(
                            CreateExpansion.MOD_ID,
                            blockId
                    );

            futures.add(
                    DataProvider.saveStable(
                            output,
                            lootTable(block),
                            lootTables.json(block)
                    )
            );
        }

        return CompletableFuture.allOf(
                futures.toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public String getName() {
        return "Create Expansion Foundry Loot Tables";
    }

    private static JsonObject lootTable(
            ResourceLocation block
    ) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", block.toString());

        JsonArray entries = new JsonArray();
        entries.add(entry);

        JsonObject condition = new JsonObject();
        condition.addProperty("condition", "minecraft:survives_explosion");

        JsonArray conditions = new JsonArray();
        conditions.add(condition);

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        pool.add("entries", entries);
        pool.add("conditions", conditions);

        JsonArray pools = new JsonArray();
        pools.add(pool);

        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:block");
        json.add("pools", pools);
        return json;
    }
}
