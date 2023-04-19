package me.kegantu.pickaxes.enchantments;

import me.kegantu.pickaxes.Pickaxes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BreakAreaEnchantment extends Enchantment {

    private static final int radius = 1;
    public static final Enchantment BREAK_AREA = registerEnchantment(new BreakAreaEnchantment());

    public BreakAreaEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 25;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != BreakArea5x5Enchantment.BREAK_AREA5x5;
    }

    private static Enchantment registerEnchantment(BreakAreaEnchantment item){
        return Registry.register(Registry.ENCHANTMENT, new Identifier(Pickaxes.MOD_ID, "breakarea"), item);
    }

    public static void loadEnchantment(){
        Pickaxes.LOGGER.debug("yay");
    }

    public static int getRadius() {
        return radius;
    }
}
