package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.spit365.lulasmod.custom.LinkedLightning;
import net.spit365.lulasmod.custom.NetherDeathSystem;
import net.spit365.lulasmod.custom.TimeForward;
import net.spit365.lulasmod.mod.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lulasmod implements ModInitializer, ClientModInitializer {
	public static final String MOD_ID = "lulasmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
	public void onInitialize() {
		ModPackets.init();
		ModSpells.init();
		ModItems.init();
		ModBlocks.init();
		ModEntities.init();
		ModParticles.init();
		ModStatusEffects.init();
		ModGamerules.init();
		ModData.init();
		ModDamageTypes.init();
		ModCommands.init();
		ModServerTick.init();
        NetherDeathSystem.init();

		LOGGER.info("Initializing for {}", MOD_ID);
	}

	@Override
	public void onInitializeClient() {
		ClientPackets.init();
		ModGui.init();
		ClientEntities.init();
		ClientParticles.init();
		ModKeybinds.init();
        TimeForward.Animator.init();
        LinkedLightning.Render.init();

		LOGGER.info("Initializing Client for {}", MOD_ID);
	}
}