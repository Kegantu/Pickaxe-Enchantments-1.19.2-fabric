package me.kegantu.pickaxes.Interface;

import me.kegantu.pickaxes.Pickaxes;

public interface IBreakBlockEnchantment {
    public static void loadEnchantment(){
        Pickaxes.LOGGER.debug("yay");
    }

    public int getRadius() ;
}
