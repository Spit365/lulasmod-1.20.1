package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class Lulasmod implements ModInitializer, ClientModInitializer {
	public static final String MOD_ID = "lulasmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModPackets.init();
		ModItems.init();
		ModSpells.init();
		ModBlocks.init();
		ModStatusEffects.init();
		ModGamerules.init();
		ModData.init();
		ModDamageSources.init();
		ModServerTick.init();
		ModCommands.init();

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			if (Objects.requireNonNull(newPlayer.getServer()).getGameRules().getBoolean(ModGamerules.NEW_DEATH_SYSTEM)) {
				ServerWorld nether = newPlayer.getServer().getWorld(World.NETHER);
				if (nether != null) {
					ServerPlayerEntity.Respawn respawn = newPlayer.getRespawn();
					BlockPos pos;
					if (respawn == null) pos = newPlayer.getBlockPos();
					else pos = respawn.pos();
					newPlayer.teleport(nether, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), newPlayer.getYaw(), newPlayer.getPitch(), false);
				}
			}
		});

		LOGGER.info("Initializing for " + Lulasmod.MOD_ID);
	}

	@Override
	public void onInitializeClient() {
		ModKeybinds.init();
		ModEntities.init();
		ModParticles.init();
		ModClientTick.init();
	}
}