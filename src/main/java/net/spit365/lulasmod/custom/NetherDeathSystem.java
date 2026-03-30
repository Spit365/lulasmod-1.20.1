package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModGamerules;

import java.util.Objects;
import java.util.Set;

public final class NetherDeathSystem {
    public static void init() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!Objects.requireNonNull(newPlayer.getServer()).getGameRules().getBoolean(ModGamerules.NEW_DEATH_SYSTEM)) return;
            ServerWorld nether = newPlayer.getServer().getWorld(World.NETHER);
            if (nether == null) return;
            ServerPlayerEntity.Respawn respawn = newPlayer.getRespawn();
            BlockPos pos;
            if (respawn == null) pos = newPlayer.getBlockPos();
            else pos = respawn.pos();
            newPlayer.teleport(nether, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, Set.of(), newPlayer.getYaw(), newPlayer.getPitch(), false);
        });
    }
}
