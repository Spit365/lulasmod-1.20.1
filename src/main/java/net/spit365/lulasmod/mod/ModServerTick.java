package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.spit365.lulasmod.custom.*;
import net.spit365.lulasmod.util.SpellHotbar;
import net.spit365.lulasmod.structures.GazeboOfSins;

public class ModServerTick {
    public static void init(){
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                MinerRepel.tick(player);
				SpellHotbar.tick(player);
                TimeForward.ServerLogic.tick(player);
			}
            Impaled.tick();
            for (ServerWorld serverWorld : server.getWorlds()){
				GazeboOfSins.tick(serverWorld);
                LinkedLightning.tick(serverWorld);
            }
		});
    }

}