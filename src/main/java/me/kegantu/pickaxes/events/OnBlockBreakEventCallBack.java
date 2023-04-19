package me.kegantu.pickaxes.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface OnBlockBreakEventCallBack {

    Event<OnBlockBreakEventCallBack> EVENT = EventFactory.createArrayBacked(OnBlockBreakEventCallBack.class, (listeners) -> (player, blockState, world, pos, blockEntity) -> {
        for (OnBlockBreakEventCallBack listener : listeners){
            ActionResult result = listener.blockBreak(player, blockState, world, pos, blockEntity);

            if(result != ActionResult.PASS) {
                return result;
            }
        }

        return ActionResult.PASS;
    });

    ActionResult blockBreak (PlayerEntity player, BlockState blockState, World world, BlockPos pos, BlockEntity blockEntity);
}
