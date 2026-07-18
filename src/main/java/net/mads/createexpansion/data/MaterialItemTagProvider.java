package net.mads.createexpansion.data;

import net.mads.createexpansion.CreateExpansion;
import net.mads.createexpansion.material.IndustrialMaterial;
import net.mads.createexpansion.material.IndustrialMaterials;
import net.mads.createexpansion.material.MaterialPart;
import net.mads.createexpansion.registry.ItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MaterialItemTagProvider extends ItemTagsProvider {
    private static final TagKey<Item> MOLDS = createExpansionTag("molds");
    private static final TagKey<Item> COLD_MOLDS = createExpansionTag("cold_molds");
    private static final TagKey<Item> HOT_MOLDS = createExpansionTag("hot_molds");

    public MaterialItemTagProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags,
            ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, blockTags, CreateExpansion.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (IndustrialMaterial material : IndustrialMaterials.ALL) {
            for (MaterialPart part : material.parts()) {
                if (!isColdMold(part) && !isHotMold(part)) {
                    continue;
                }

                addMoldTags(material, part);
            }
        }
    }

    private void addMoldTags(IndustrialMaterial material, MaterialPart part) {
        TagKey<Item> temperatureTag = isHotMold(part) ? HOT_MOLDS : COLD_MOLDS;
        String shape = moldShape(part);

        addItem(material, part, MOLDS);
        addItem(material, part, temperatureTag);
        addItem(material, part, createExpansionTag("molds/" + shape));
        addItem(material, part, createExpansionTag((isHotMold(part) ? "hot_molds/" : "cold_molds/") + shape));
        addItem(material, part, createExpansionTag("molds/" + material.id()));
        addItem(material, part, createExpansionTag((isHotMold(part) ? "hot_molds/" : "cold_molds/") + material.id()));
    }

    private void addItem(IndustrialMaterial material, MaterialPart part, TagKey<Item> tag) {
        if (material.hasExistingPart(part)) {
            tag(tag).addOptional(material.existingPart(part));
            return;
        }

        var item = ItemRegistry.getMaterialItem(material, part);
        if (item != null) {
            tag(tag).add(item.get());
        }
    }

    private static boolean isColdMold(MaterialPart part) {
        return part.name().startsWith("CAST_") && part.name().endsWith("_MOLD");
    }

    private static boolean isHotMold(MaterialPart part) {
        return part.name().startsWith("HOT_CAST_") && part.name().endsWith("_MOLD");
    }

    private static String moldShape(MaterialPart part) {
        String id = part.id();
        if (id.startsWith("hot_cast_")) {
            id = id.substring("hot_cast_".length());
        } else if (id.startsWith("cast_")) {
            id = id.substring("cast_".length());
        }
        if (id.endsWith("_mold")) {
            id = id.substring(0, id.length() - "_mold".length());
        }
        return id;
    }

    private static TagKey<Item> createExpansionTag(String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(CreateExpansion.MOD_ID, path));
    }
}
