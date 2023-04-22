package me.kegantu.pickaxes;


import me.kegantu.pickaxes.config.pickaxesEnchantmentsConfig;
import me.kegantu.pickaxes.enchantments.BreakArea5x5Enchantment;
import me.kegantu.pickaxes.enchantments.BreakAreaEnchantment;
import me.kegantu.pickaxes.enchantments.BreakArea21x21Enchantment;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Pickaxes implements ModInitializer {
	public static final pickaxesEnchantmentsConfig CONFIG = pickaxesEnchantmentsConfig.createAndLoad();
	public static final String MOD_ID = "pickaxes";
	public static final Logger LOGGER = LoggerFactory.getLogger("pickaxes");


	@Override
	public void onInitialize() {
		EventHandler.initializeEvent();

		BreakAreaEnchantment.loadEnchantment();
		BreakArea5x5Enchantment.loadEnchantment();
		BreakArea21x21Enchantment.loadEnchantment();
	}
}