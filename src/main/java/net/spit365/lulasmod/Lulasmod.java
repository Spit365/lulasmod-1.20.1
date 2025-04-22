package net.spit365.lulasmod;

import net.fabricmc.api.ModInitializer;
import net.spit365.lulasmod.mod.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lulasmod implements ModInitializer {
	public static final String MOD_ID = "lulasmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModTagCategories.init();
		ModItems.init();
		ModItemGroups.init();
		ModEntities.init();
		ModStatusEffects.init();
		ModPackets.init();
		ModServerEvents.init();
		ModCommands.init();
		ModDimensions.init();
		LOGGER.info("Initializing for " + Lulasmod.MOD_ID);
	}
}