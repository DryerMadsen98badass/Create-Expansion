package net.mads.createexpansion.machine.machines.kinetic.wiredrawer;

import net.mads.createexpansion.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class KineticWireDrawerPartBlockEntity extends BlockEntity {
    private BlockPos controllerPos;

    public KineticWireDrawerPartBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.KINETIC_WIRE_DRAWER_PART.get(), pos, state);
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        setChanged();
    }

    @Nullable
    public BlockPos controllerPos() {
        return controllerPos;
    }

    @Nullable
    public KineticWireDrawerBlockEntity controller() {
        if (level == null || controllerPos == null) {
            return null;
        }
        if (level.getBlockEntity(controllerPos) instanceof KineticWireDrawerBlockEntity wireDrawer) {
            return wireDrawer;
        }
        return null;
    }

    @Nullable
    public IItemHandler itemCapability() {
        KineticWireDrawerBlockEntity wireDrawer = controller();
        return wireDrawer == null ? null : wireDrawer.itemCapability();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (controllerPos != null) {
            tag.putInt("ControllerX", controllerPos.getX());
            tag.putInt("ControllerY", controllerPos.getY());
            tag.putInt("ControllerZ", controllerPos.getZ());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("ControllerX")) {
            controllerPos = new BlockPos(tag.getInt("ControllerX"), tag.getInt("ControllerY"), tag.getInt("ControllerZ"));
        } else {
            controllerPos = null;
        }
    }
}
