package me.kegantu.pickaxes.mixins;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.kegantu.pickaxes.BreakingBlockObject.AppendedObjectIterator;
import me.kegantu.pickaxes.Interface.IBreakBlockEnchantment;
import me.kegantu.pickaxes.Pickaxes;
import me.kegantu.pickaxes.behavior.BreakAreaEnchantmentBehavior;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BlockBreakingInfo;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;
import java.util.List;

@Mixin(WorldRenderer.class)
@Environment(EnvType.CLIENT)
public class WorldRendererMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;
    @Shadow @Final private Long2ObjectMap<SortedSet<BlockBreakingInfo>> blockBreakingProgressions;

    @Inject(at = @At("HEAD"), method = "drawBlockOutline", cancellable = true)
    private void drawBlockOutline(MatrixStack stack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo ci) {

        int radius = 0;

        if (!Pickaxes.CONFIG.enableOutline()){
            return;
        }

        ItemStack heldStack = this.client.player.getInventory().getMainHandStack();

        HashSet<BlockPos> positions = new HashSet<>();
        /*if (EnchantmentHelper.getLevel(BreakAreaEnchantment.BREAK_AREA, heldStack) > 0){
            positions = BreakAreaEnchantmentBehavior.blockPositions(blockPos, BreakAreaEnchantment.getRadius(), true);
        } else if (EnchantmentHelper.getLevel(BreakArea5x5Enchantment.BREAK_AREA5x5, heldStack) > 0) {
            positions = BreakAreaEnchantmentBehavior.blockPositions(blockPos, BreakArea5x5Enchantment.getRadius(), true);
        } else if (EnchantmentHelper.getLevel(OhFuckEnchantment.BREAK_AREA21x21, heldStack) > 0) {
            positions = BreakAreaEnchantmentBehavior.blockPositions(blockPos, OhFuckEnchantment.getRadius(), true);
        }*/

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(heldStack);

        for (Enchantment enchantment : enchantments.keySet()) {
            if (enchantment instanceof IBreakBlockEnchantment breakBlockEnchantment){
                radius = breakBlockEnchantment.getRadius();

                positions = BreakAreaEnchantmentBehavior.blockPositions(blockPos, radius, true);

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

                if(!client.player.getAbilities().creativeMode){
                    if(!client.player.isSneaking()){
                        if (client.crosshairTarget instanceof BlockHitResult crosshairTarget) {

                            BlockPos crosshairPos = crosshairTarget.getBlockPos();

                            if (world.getBlockState(crosshairPos).getMaterial() == Material.STONE){

                                if (world.getBlockState(crosshairPos).getBlock() != Blocks.BEDROCK){
                                    for (BlockPos position : positions) {
                                        BlockPos diffPos = position.subtract(crosshairPos);
                                        BlockState offsetShape = world.getBlockState(position);

                                        if (world.getBlockState(position).getMaterial() == Material.STONE){
                                            outlineShapes.set(0, VoxelShapes.union(outlineShapes.get(0), offsetShape.getOutlineShape(world, position).offset(diffPos.getX(), diffPos.getY(), diffPos.getZ())));
                                        }
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
            }
        }


    }

    @Invoker("drawCuboidShapeOutline")
    public static void drawCuboidShapeOutline(MatrixStack matrices, VertexConsumer vertexConsumer, VoxelShape shape, double offsetX, double offsetY, double offsetZ, float red, float green, float blue, float alpha) {
        throw new AssertionError();
    }

    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;iterator()Lit/unimi/dsi/fastutil/objects/ObjectIterator;",
            shift = At.Shift.BY, by = 2), ordinal = 0)
    private ObjectIterator<Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>>> appendBreakingBlock(ObjectIterator<Long2ObjectMap.Entry<SortedSet<BlockBreakingInfo>>> originalIterator){
        return new AppendedObjectIterator(originalIterator, this.getCurrentExtraBreakingInfos());
    }

    @Unique
    private Long2ObjectMap<BlockBreakingInfo> getCurrentExtraBreakingInfos() {
        assert client.player != null;

        ItemStack heldStack = this.client.player.getInventory().getMainHandStack();
        Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.get(heldStack);

        if (!Pickaxes.CONFIG.enableBreakingBlockAnimation()){
            return Long2ObjectMaps.emptyMap();
        }

        // make sure we should display the outline based on the tool
        for (Enchantment enchantment1 : enchantmentMap.keySet()) {
            if (enchantment1 instanceof IBreakBlockEnchantment){
                // check if we should display the outline based on config and sneaking
                if (!client.player.isSneaking()) {
                    HitResult crosshairTarget = client.crosshairTarget;

                    // ensure we're not displaying an outline on a creeper or air
                    if (crosshairTarget instanceof BlockHitResult) {
                        BlockPos crosshairPos = ((BlockHitResult) crosshairTarget).getBlockPos();
                        SortedSet<BlockBreakingInfo> infos = this.blockBreakingProgressions.get(crosshairPos.asLong());

                        // make sure current block breaking progress is valid
                        if (infos != null && !infos.isEmpty() && world.getBlockState(crosshairPos).getMaterial() == Material.STONE) {
                            BlockBreakingInfo breakingInfo = infos.last();
                            int stage = breakingInfo.getStage();
                            int radius = 0;

                            Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(heldStack);

                            for (Enchantment enchantment : enchantments.keySet()) {
                                if (enchantment instanceof IBreakBlockEnchantment breakBlockEnchantment) {
                                    radius = breakBlockEnchantment.getRadius();
                                }
                            }

                            // collect positions for displaying outlines at
                            HashSet<BlockPos> positions = BreakAreaEnchantmentBehavior.blockPositions(crosshairPos, radius, true);
                            Long2ObjectMap<BlockBreakingInfo> map = new Long2ObjectLinkedOpenHashMap<>(positions.size());

                            // filter positions
                            for (BlockPos position : positions) {
                                if(world.getBlockState(position).getMaterial() == Material.STONE) {
                                    BlockBreakingInfo info = new BlockBreakingInfo(breakingInfo.hashCode(), position);
                                    info.setStage(stage);
                                    map.put(position.asLong(), info);
                                }
                            }

                            return map;
                        }
                    }
                }
            }
        }

        return Long2ObjectMaps.emptyMap();
    }
}
