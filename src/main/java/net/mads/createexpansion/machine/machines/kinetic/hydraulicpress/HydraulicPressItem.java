package net.mads.createexpansion.machine.machines.kinetic.hydraulicpress;

import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class HydraulicPressItem extends BlockItem {
    public HydraulicPressItem(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        if (level.getBlockEntity(clickedPos) instanceof DepotBlockEntity) {
            BlockPlaceContext redirected = BlockPlaceContext.at(
                    new BlockPlaceContext(context),
                    clickedPos.above(2),
                    Direction.DOWN
            );
            if (level.getBlockState(redirected.getClickedPos()).canBeReplaced(redirected)) {
                return super.useOn(redirected);
            }
        }
        return super.useOn(context);
    }
}
