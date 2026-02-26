package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.spit365.lulasmod.custom.*;
import net.spit365.lulasmod.structure.GazeboOfSins;
import net.spit365.lulasmod.util.SpellHotbar;

public final class ModServerTick {
    public static void init(){
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                MinerRepel.tick(player);
				SpellHotbar.tick(player);
                TimeForward.tick(player);
				DashSpell.tick(player);
                BoxOutline.tick(player);
                Shimmer.tick(player);
			}
            Impaled.tick();
            for (ServerWorld serverWorld : server.getWorlds()){
				GazeboOfSins.tick(serverWorld);
                LinkedLightning.tick(serverWorld);
                Bleed.tick(serverWorld);
                SmokeSpellCooldown.tick(serverWorld);
            }
		});
    }

}