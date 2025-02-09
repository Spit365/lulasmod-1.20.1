package net.spit365.lulasmod;

import net.fabricmc.api.ModInitializer;

import net.spit365.lulasmod.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lulasmod implements ModInitializer {
	public static final String MOD_ID = "lulasmod";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.init();
		ModEntities.init();
		ModParticles.init();
		ModEvents.init();
		LOGGER.info("Initializing for " + Lulasmod.MOD_ID);
	}

}