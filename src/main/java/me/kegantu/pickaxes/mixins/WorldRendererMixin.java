package me.kegantu.pickaxes.mixins;

import me.kegantu.pickaxes.Pickaxes;
import me.kegantu.pickaxes.behavior.BreakAreaEnchantmentBehavior;
import me.kegantu.pickaxes.enchantments.BreakArea5x5Enchantment;
import me.kegantu.pickaxes.enchantments.BreakAreaEnchantment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void drawBlockOutline(MatrixStack stack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        if (!Pickaxes.CONFIG.enableOutline()){
            return;
        }

        ItemStack heldStack = this.client.player.getInventory().getMainHandStack();

        HashSet<BlockPos> positions = new HashSet<>();
        if (EnchantmentHelper.getLevel(BreakAreaEnchantment.BREAK_AREA, heldStack) > 0){
            positions = BreakAreaEnchantmentBehavior.blockPositions(blockPos, BreakAreaEnchantment.getRadius(), true);
        } else if (EnchantmentHelper.getLevel(BreakArea5x5Enchantment.BREAK_AREA5x5, heldStack) > 0) {
            positions = BreakAreaEnchantmentBehavior.blockPositions(blockPos, BreakArea5x5Enchantment.getRadius(), true);
        }

        List<VoxelShape> outlineShapes = new ArrayList<>();
        outlineShapes.add(VoxelShapes.empty());

        // ensure player is not null
        if(this.client.player == null) {
            return;
        }

        // ensure world is not null
        if(this.client.world == null) {
            return;
        }

        if (EnchantmentHelper.getLevel(BreakAreaEnchantment.BREAK_AREA, heldStack) > 0 || EnchantmentHelper.getLevel(BreakArea5x5Enchantment.BREAK_AREA5x5, heldStack) > 0){
            if (client.crosshairTarget instanceof BlockHitResult crosshairTarget) {

                BlockPos crosshairPos = crosshairTarget.getBlockPos();

                if (world.getBlockState(crosshairPos).getMaterial() == Material.STONE){

                    if (world.getBlockState(crosshairPos).getBlock() != Blocks.BEDROCK){
                        for (BlockPos position : positions) {
                            BlockPos diffPos = position.subtract(crosshairPos);
                            BlockState offsetShape = world.getBlockState(position);

                            outlineShapes.set(0, VoxelShapes.union(outlineShapes.get(0), offsetShape.getOutlineShape(world, position).offset(diffPos.getX(), diffPos.getY(), diffPos.getZ())));
                        }

                        outlineShapes.forEach(shape -> {
                            // draw extended hitbox
                            drawCuboidShapeOutline(
                                    stack,
                                    vertexConsumer,
                                    shape, // blockState.getOutlineShape(this.world, blockPos, ShapeContext.of(entity))
                                    (double) crosshairPos.getX() - d,
                                    (double) crosshairPos.getY() - e,
                                    (double) crosshairPos.getZ() - f,
                                    Pickaxes.CONFIG.color.red(),
                                    Pickaxes.CONFIG.color.green(),
                                    Pickaxes.CONFIG.color.blue(),
                                    Pickaxes.CONFIG.color.alpha());
                        });

                        ci.cancel();
                    }
                }
            }
        }
    }

    @Invoker("drawCuboidShapeOutline")
    public static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        throw new AssertionError();
    }

}
