package net.mads.createexpansion.client.model;

import com.simibubi.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.model.BakedModelWrapperWithData;
import net.createmod.catnip.data.Iterate;
import net.mads.createexpansion.CreateExpansionPartialModels;
import net.mads.createexpansion.transport.FluidTransportTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FluidTransportPipeAttachmentModel extends BakedModelWrapperWithData {
    private static final ModelProperty<PipeModelData> PIPE_PROPERTY = new ModelProperty<>();

    private final FluidTransportTier tier;

    public FluidTransportPipeAttachmentModel(BakedModel template, FluidTransportTier tier) {
        super(template);
        this.tier = tier;
    }

    @Override
    protected ModelData.Builder gatherModelData(
            ModelData.Builder builder,
            BlockAndTintGetter world,
            BlockPos pos,
            BlockState state,
            ModelData blockEntityData
    ) {
        PipeModelData data = new PipeModelData();
        FluidTransportBehaviour transport = BlockEntityBehaviour.get(world, pos, FluidTransportBehaviour.TYPE);
        BracketedBlockEntityBehaviour bracket = BlockEntityBehaviour.get(world, pos, BracketedBlockEntityBehaviour.TYPE);

        if (transport != null) {
            for (Direction direction : Iterate.directions) {
                data.putAttachment(direction, transport.getRenderedRimAttachment(world, pos, state, direction));
            }
        }
        if (bracket != null) {
            data.putBracket(bracket.getBracket());
        }

        data.setEncased(FluidPipeBlock.shouldDrawCasing(world, pos, state));
        return builder.with(PIPE_PROPERTY, data);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(
            @NotNull BlockState state,
            @NotNull RandomSource random,
            @NotNull ModelData modelData
    ) {
        List<ChunkRenderTypeSet> renderTypes = new ArrayList<>();
        renderTypes.add(super.getRenderTypes(state, random, modelData));
        renderTypes.add(CreateExpansionPartialModels.fluidPipeCasing(tier).get()
                .getRenderTypes(state, random, modelData));

        if (modelData.has(PIPE_PROPERTY)) {
            PipeModelData data = modelData.get(PIPE_PROPERTY);
            for (Direction direction : Iterate.directions) {
                FluidTransportBehaviour.AttachmentTypes attachment = data.getAttachment(direction);
                for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : attachment.partials) {
                    renderTypes.add(CreateExpansionPartialModels.pipeAttachment(tier, partial, direction).get()
                            .getRenderTypes(state, random, modelData));
                }
            }
        }

        return ChunkRenderTypeSet.union(renderTypes);
    }

    @Override
    public List<BakedQuad> getQuads(
            BlockState state,
            Direction side,
            RandomSource random,
            ModelData modelData,
            RenderType renderType
    ) {
        List<BakedQuad> quads = super.getQuads(state, side, random, modelData, renderType);
        if (!modelData.has(PIPE_PROPERTY)) {
            return quads;
        }

        PipeModelData data = modelData.get(PIPE_PROPERTY);
        quads = new ArrayList<>(quads);

        BakedModel bracket = data.getBracket();
        if (bracket != null) {
            quads.addAll(bracket.getQuads(state, side, random, modelData, renderType));
        }

        for (Direction direction : Iterate.directions) {
            FluidTransportBehaviour.AttachmentTypes attachment = data.getAttachment(direction);
            for (FluidTransportBehaviour.AttachmentTypes.ComponentPartials partial : attachment.partials) {
                quads.addAll(CreateExpansionPartialModels.pipeAttachment(tier, partial, direction).get()
                        .getQuads(state, side, random, modelData, renderType));
            }
        }

        if (data.isEncased()) {
            quads.addAll(CreateExpansionPartialModels.fluidPipeCasing(tier).get()
                    .getQuads(state, side, random, modelData, renderType));
        }

        return quads;
    }

    @Override
    public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType renderType) {
        return TriState.TRUE;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    private static final class PipeModelData {
        private final FluidTransportBehaviour.AttachmentTypes[] attachments =
                new FluidTransportBehaviour.AttachmentTypes[6];
        private boolean encased;
        private BakedModel bracket;

        private PipeModelData() {
            Arrays.fill(attachments, FluidTransportBehaviour.AttachmentTypes.NONE);
        }

        private void putBracket(BlockState state) {
            if (state != null) {
                bracket = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            }
        }

        private BakedModel getBracket() {
            return bracket;
        }

        private void putAttachment(Direction direction, FluidTransportBehaviour.AttachmentTypes attachment) {
            attachments[direction.get3DDataValue()] = attachment;
        }

        private FluidTransportBehaviour.AttachmentTypes getAttachment(Direction direction) {
            return attachments[direction.get3DDataValue()];
        }

        private void setEncased(boolean encased) {
            this.encased = encased;
        }

        private boolean isEncased() {
            return encased;
        }
    }
}
