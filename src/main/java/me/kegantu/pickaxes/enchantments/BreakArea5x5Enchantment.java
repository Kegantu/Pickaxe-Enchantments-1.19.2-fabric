package me.kegantu.pickaxes.enchantments;

import me.kegantu.pickaxes.Pickaxes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BreakArea5x5Enchantment extends Enchantment {

    private static final int radius = 2;
    public static final Enchantment BREAK_AREA5x5 = registerEnchantment(new BreakArea5x5Enchantment());

    public BreakArea5x5Enchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 30;
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
        return super.canAccept(other) && other != BreakAreaEnchantment.BREAK_AREA;
    }

    private static Enchantment registerEnchantment(BreakArea5x5Enchantment item){
        return Registry.register(Registry.ENCHANTMENT, new Identifier(Pickaxes.MOD_ID, "breakarea5x5"), item);
    }

    public static void loadEnchantment(){
        Pickaxes.LOGGER.debug("yay");
    }


    public static int getRadius() {
        return radius;
    }
}
