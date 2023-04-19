package me.kegantu.pickaxes.behavior;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.HashSet;

public class BreakAreaEnchantmentBehavior {

    public static HashSet<BlockPos> blockPositions(BlockPos blockBrokeByPickaxes, int size, boolean outLine){

        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        BlockHitResult blockHit = (BlockHitResult) hit;

        HashSet<BlockPos> positions = new HashSet<>();

        if (blockHit.getSide() == Direction.EAST || blockHit.getSide() == Direction.WEST){
            for (int i = -size; i <= size; i++) {
                for (int j = -size; j <= size; j++) {
                    if (i == 0 && j == 0 && !outLine){
                        continue;
                    }
                    positions.add(new BlockPos(blockBrokeByPickaxes.getX(), blockBrokeByPickaxes.getY() + j, blockBrokeByPickaxes.getZ() + i));
                }
            }
        }
        else if(blockHit.getSide() == Direction.SOUTH || blockHit.getSide() == Direction.NORTH){
            for (int i = -size; i <= size; i++) {
                for (int j = -size; j <= size; j++) {
                    if (i == 0 && j == 0 && !outLine){
                        continue;
                    }
                    positions.add(new BlockPos(blockBrokeByPickaxes.getX() + i, blockBrokeByPickaxes.getY() + j, blockBrokeByPickaxes.getZ()));
                }
            }
        } else if(blockHit.getSide() == Direction.DOWN || blockHit.getSide() == Direction.UP) {
            for (int i = -size; i <= size; i++) {
                for (int j = -size; j <= size; j++) {
                    if (i == 0 && j == 0 && !outLine){
                        continue;
                    }
                    positions.add(new BlockPos(blockBrokeByPickaxes.getX() + i, blockBrokeByPickaxes.getY(), blockBrokeByPickaxes.getZ() + j));
                }
            }
        }

        return positions;
    }
}
