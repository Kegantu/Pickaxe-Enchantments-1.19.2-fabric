package me.kegantu.pickaxes.mixins;

import me.kegantu.pickaxes.EventHandler;
import me.kegantu.pickaxes.Interface.IBreakBlockEnchantment;
import me.kegantu.pickaxes.events.OnBlockBreakEventCallBack;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = ServerPlayerInteractionManager.class, priority = 1001)
public class OnBlockBreakEventMixin {

    @Shadow public ServerPlayerEntity player;
    @Shadow
    public ServerWorld world;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBreak(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/player/PlayerEntity;)V"), method = "tryBreakBlock", cancellable = true)
    private void tryBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        ItemStack heldItem = player.getMainHandStack();
        Map<Enchantment, Integer> enchantmentMap = EnchantmentHelper.get(heldItem);

        for (Enchantment enchantment : enchantmentMap.keySet()){
            if (enchantment instanceof IBreakBlockEnchantment){
                if(EventHandler.isReadyToBreak){
                    OnBlockBreakEventCallBack.EVENT.invoker().blockBreak(player, world.getBlockState(pos), world, pos, world.getBlockEntity(pos));
                }
            }
        }
    }
}
