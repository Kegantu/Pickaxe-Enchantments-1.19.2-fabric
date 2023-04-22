package me.kegantu.pickaxes;

import me.kegantu.pickaxes.Interface.IBreakBlockEnchantment;
import me.kegantu.pickaxes.behavior.BreakAreaEnchantmentBehavior;
import me.kegantu.pickaxes.events.OnBlockBreakEventCallBack;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class EventHandler {

    public static boolean isReadyToBreak = true;

    public static void initializeEvent(){
        AttackBlockCallback.EVENT.register(((player, world, hand, pos, direction) -> {

            isReadyToBreak = true;

            return ActionResult.PASS;
        }));

        OnBlockBreakEventCallBack.EVENT.register(((player, blockState, world, pos, blockEntity) -> {

            isReadyToBreak = false;

            int radius = 0;

            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(player.getMainHandStack());

            for (Enchantment enchantment : enchantments.keySet()) {
                if (enchantment instanceof IBreakBlockEnchantment breakBlockEnchantment){
                    radius = breakBlockEnchantment.getRadius();
                }
            }

            if (blockState.getMaterial() == Material.STONE) {
                for (BlockPos _pos : BreakAreaEnchantmentBehavior.blockPositions(pos, radius, false)) {
                    TryBreakBlock(_pos, world, world.getBlockState(_pos), player);
                }
            }

            return ActionResult.PASS;
        }));
    }

    private static boolean TryBreakBlock(BlockPos pos, World world, BlockState blockState, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Block block = blockState.getBlock();
        if (blockState.getMaterial() != Material.STONE || blockState.getBlock() == Blocks.BEDROCK) {
            return true;
        }

        if(player.isSneaking()){
            return true;
        }

        block.onBreak(world, pos, blockState, player);
        boolean bl = world.removeBlock(pos, false);
        if (player.isCreative()) {
            return true;
        } else {
            ItemStack itemStack = player.getMainHandStack();
            ItemStack itemStack2 = itemStack.copy();
            boolean bl2 = player.canHarvest(blockState);
            itemStack.postMine(world, blockState, pos, player);
            if (bl && bl2) {
                block.afterBreak(world, player, pos, blockState, blockEntity, itemStack2);
            }

            return true;
        }
    }
}
