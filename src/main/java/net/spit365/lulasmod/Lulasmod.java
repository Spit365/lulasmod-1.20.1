package net.spit365.lulasmod;

import net.fabricmc.api.ModInitializer;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lulasmod implements ModInitializer {
	public static final String MOD_ID = "lulasmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean creeperExplode = false;
	public static String whoExplode = "MEGAMASTER75983";

	@Override
	public void onInitialize() {
		ModItems.init();
		ModEntities.init();
		ModParticles.init();
		ModEvents.init();
		LOGGER.info("Initializing done");
	}
	public static void summonSmoke(Vec3d position, World world){
		((ServerWorld) world).spawnParticles(
				ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
				position.x, position.y +  1, position.z,
				269, 1.2, 1.2, 1.2, 0
		);
	}

}