package net.mads.createexpansion.block;

import net.mads.createexpansion.CreateExpansion;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record ActiveBlockDefinition(
        String id,
        String displayName,
        ResourceLocation offTexture,
        ResourceLocation onTexture,
        MiningTier miningTier,
        Set<MiningTool> miningTools
) {

    public ActiveBlockDefinition(
            String id,
            String displayName,
            String offTexture,
            String onTexture
    ) {
        this(
                id,
                displayName,
                parseTexture(offTexture),
                parseTexture(onTexture),
                MiningTier.STONE,
                EnumSet.of(MiningTool.PICKAXE)
        );
    }

    public ActiveBlockDefinition {
        validateId(id);
        validateDisplayName(id, displayName);

        if (offTexture == null) {
            throw new IllegalArgumentException(
                    "Active block off texture cannot be null: "
                            + id
            );
        }

        if (onTexture == null) {
            throw new IllegalArgumentException(
                    "Active block on texture cannot be null: "
                            + id
            );
        }

        if (miningTier == null) {
            throw new IllegalArgumentException(
                    "Active block mining tier cannot be null: "
                            + id
            );
        }

        miningTools = validateMiningTools(
                id,
                miningTools
        );
    }

    public ActiveBlockDefinition wood() {
        return withMiningTier(
                MiningTier.WOOD
        );
    }

    public ActiveBlockDefinition stone() {
        return withMiningTier(
                MiningTier.STONE
        );
    }

    public ActiveBlockDefinition iron() {
        return withMiningTier(
                MiningTier.IRON
        );
    }

    public ActiveBlockDefinition diamond() {
        return withMiningTier(
                MiningTier.DIAMOND
        );
    }

    public ActiveBlockDefinition netherite() {
        return withMiningTier(
                MiningTier.NETHERITE
        );
    }

    public ActiveBlockDefinition withMiningTier(
            MiningTier tier
    ) {
        if (tier == null) {
            throw new IllegalArgumentException(
                    "Active block mining tier cannot be null: "
                            + id
            );
        }

        return new ActiveBlockDefinition(
                id,
                displayName,
                offTexture,
                onTexture,
                tier,
                miningTools
        );
    }

    public ActiveBlockDefinition mineableWith(
            MiningTool tool,
            MiningTool... moreTools
    ) {
        if (tool == null) {
            throw new IllegalArgumentException(
                    "Active block mining tool cannot be null: "
                            + id
            );
        }

        EnumSet<MiningTool> updated =
                EnumSet.of(tool);

        if (moreTools != null) {
            for (MiningTool moreTool : moreTools) {
                if (moreTool == null) {
                    throw new IllegalArgumentException(
                            "Active block mining tool cannot be null: "
                                    + id
                    );
                }

                updated.add(moreTool);
            }
        }

        return new ActiveBlockDefinition(
                id,
                displayName,
                offTexture,
                onTexture,
                miningTier,
                updated
        );
    }

    public float hardness() {
        return miningTier.hardness();
    }

    public float resistance() {
        return miningTier.resistance();
    }

    public String modelPath(
            boolean active
    ) {
        return "block/casings/active/"
                + id
                + (active ? "_on" : "_off");
    }

    public ResourceLocation texture(
            boolean active
    ) {
        return active
                ? onTexture
                : offTexture;
    }

    private static ResourceLocation parseTexture(
            String texture
    ) {
        if (texture == null
                || texture.isBlank()) {
            throw new IllegalArgumentException(
                    "Active block texture cannot be blank"
            );
        }

        if (texture.contains(":")) {
            ResourceLocation parsed =
                    ResourceLocation.tryParse(texture);

            if (parsed == null) {
                throw new IllegalArgumentException(
                        "Invalid active block texture: "
                                + texture
                );
            }

            return parsed;
        }

        if (!ResourceLocation.isValidPath(texture)) {
            throw new IllegalArgumentException(
                    "Invalid active block texture path: "
                            + texture
            );
        }

        return ResourceLocation.fromNamespaceAndPath(
                CreateExpansion.MOD_ID,
                texture
        );
    }

    private static void validateId(
            String id
    ) {
        if (id == null
                || id.isBlank()
                || !ResourceLocation.isValidPath(id)) {
            throw new IllegalArgumentException(
                    "Invalid active block id: "
                            + id
            );
        }
    }

    private static void validateDisplayName(
            String id,
            String displayName
    ) {
        if (displayName == null
                || displayName.isBlank()) {
            throw new IllegalArgumentException(
                    "Active block display name cannot be blank: "
                            + id
            );
        }
    }

    private static Set<MiningTool> validateMiningTools(
            String id,
            Set<MiningTool> miningTools
    ) {
        if (miningTools == null
                || miningTools.isEmpty()) {
            throw new IllegalArgumentException(
                    "Active block mining tools cannot be empty: "
                            + id
            );
        }

        if (miningTools.contains(null)) {
            throw new IllegalArgumentException(
                    "Active block mining tools cannot contain null: "
                            + id
            );
        }

        return Collections.unmodifiableSet(
                EnumSet.copyOf(miningTools)
        );
    }
}