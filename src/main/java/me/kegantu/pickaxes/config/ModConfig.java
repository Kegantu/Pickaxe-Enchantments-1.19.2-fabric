package me.kegantu.pickaxes.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;
import io.wispforest.owo.config.annotation.Nest;
import io.wispforest.owo.config.annotation.RangeConstraint;
import me.kegantu.pickaxes.Pickaxes;

import java.awt.*;


@Modmenu(modId = Pickaxes.MOD_ID)
@Config(name = "pickaxesEnchantments", wrapperName = "pickaxesEnchantmentsConfig")
public class ModConfig {
    public boolean enableOutline = true;

    public boolean enableBreakingBlockAnimation = false;

    @Nest
    public Color color = new Color();

    public static class Color{
        @RangeConstraint(min = 0.0f, max = 100.0f)
        public float red = 0.0f;
        @RangeConstraint(min = 0.0f, max = 100.0f)
        public float green = 0.0f;
        @RangeConstraint(min = 0.0f, max = 100.0f)
        public float blue = 0.0f;
        @RangeConstraint(min = 0.0f, max = 1.0f)
        public float alpha = 0.4f;
    }
}
