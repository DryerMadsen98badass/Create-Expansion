package net.mads.createexpansion.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.transport.FluidTransportTier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class FluidTransportLootProvider implements DataProvider {
    private final PackOutput output;

    public FluidTransportLootProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        Path lootTables = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(CreateExpansion.MOD_ID)
                .resolve("loot_table/blocks");

        for (FluidTransportTier tier : FluidTransportTier.all()) {
            futures.add(save(cache, lootTables, tier.pipeId(), tier.pipeId()));
            futures.add(save(cache, lootTables, tier.glassPipeId(), tier.pipeId()));
            futures.add(save(cache, lootTables, tier.pumpId(), tier.pumpId()));
            futures.add(save(cache, lootTables, tier.tankId(), tier.tankId()));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Create Expansion Fluid Transport Loot Tables";
    }

    private static CompletableFuture<?> save(
            CachedOutput cache,
            Path lootTables,
            String blockId,
            String droppedItemId
    ) {
        return DataProvider.saveStable(
                cache,
                lootTable(droppedItemId),
                lootTables.resolve(blockId + ".json")
        );
    }

    private static JsonObject lootTable(String droppedItemId) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", CreateExpansion.MOD_ID + ":" + droppedItemId);

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
