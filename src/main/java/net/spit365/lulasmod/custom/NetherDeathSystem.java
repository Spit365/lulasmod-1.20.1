package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.mod.ModGamerules;

import java.util.Objects;
import java.util.Set;

public final class NetherDeathSystem {
    public static void init() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            MinecraftServer server = newPlayer.level().getServer();
	        if (!server.getGameRules().get(ModGamerules.NEW_DEATH_SYSTEM)) return;
            ServerLevel nether = server.getLevel(Level.NETHER);
            if (nether == null) return;
            ServerPlayer.RespawnConfig respawn = newPlayer.getRespawnConfig();
            BlockPos pos;
            if (respawn == null) pos = newPlayer.blockPosition();
            else pos = respawn.respawnData().pos();
            newPlayer.teleportTo(nether, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), newPlayer.getYRot(), newPlayer.getXRot(), false);
        });
    }
}
