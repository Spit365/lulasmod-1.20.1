package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.spit365.lulasmod.custom.*;
import net.spit365.lulasmod.structure.GazeboOfSins;
import net.spit365.lulasmod.util.SpellHotbar;

public final class ModServerTick {
    public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                MinerRepel.tick(player);
				SpellHotbar.tick(player);
                TimeForward.tick(player);
				DashSpell.tick(player);
                Shimmer.tick(player);
			}
            Impaled.tick();
            for (ServerLevel serverWorld : server.getAllLevels()) {
				GazeboOfSins.tick(serverWorld);
                LinkedLightning.tick(serverWorld);
                Bleed.tick(serverWorld);
                SmokeSpellCooldown.tick(serverWorld);
            }
		});
    }

}