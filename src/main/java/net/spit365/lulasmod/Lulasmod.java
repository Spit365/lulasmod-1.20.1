package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.spit365.lulasmod.mod.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lulasmod implements ModInitializer, ClientModInitializer {
	public static final String MOD_ID = "lulasmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int CURRENT_UPDATE_RANGE = 1000;

    @Override
	public void onInitialize() {
		ModPackets.init();
		ModSpells.init();
		ModItems.init();
		ModBlocks.init();
		ModStatusEffects.init();
		ModGamerules.init();
		ModData.init();
		ModDamageSources.init();
        ModStructures.init();
		ModServerEvents.init();
		ModCommands.init();

		LOGGER.info("Initializing for {}", MOD_ID);
	}

	@Override
	public void onInitializeClient() {
		ModGui.init();
		ModKeybinds.init();
		ModEntities.init();
		ModParticles.init();
		ModClientEvents.init();

		LOGGER.info("Initializing Client for {}", MOD_ID);
	}
}