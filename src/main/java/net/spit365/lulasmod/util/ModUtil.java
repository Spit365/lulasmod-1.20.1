package net.spit365.lulasmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ModUtil {
    public static @Nullable Entity selectClosestEntity(Entity selector, double radius) {
        Vec3 selectionCenter = selector.getViewVector(1).normalize().scale(radius).add(selector.position());
        Entity selectedEntity = null;
        for (Entity entityInRange : selector.level().getEntities(selector, new AABB(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))) {
            if (selectedEntity == null || selectedEntity.position().distanceToSqr(selector.position()) > entityInRange.position().distanceToSqr(selector.position()))
                selectedEntity = entityInRange;
        }
        return selectedEntity;
    }

	public static void sendHome(Player player, Item item) {
		try {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
			MinecraftServer server = Objects.requireNonNull(player.getServer());
			ServerPlayer.RespawnConfig respawn = serverPlayer.getRespawnConfig();
			ServerLevel targetDimension = Objects.requireNonNull(server.getLevel(respawn != null ? respawn.dimension() : Level.OVERWORLD));
			BlockPos pos = respawn != null ? respawn.pos() : targetDimension.getSharedSpawnPos();
			if (player.teleportTo(targetDimension, pos.getX(), pos.getY(), pos.getZ(), Set.of(), player.getYRot(), player.getXRot(), true))
				Lulasmod.LOGGER.info("{} was sent home to {} {} {} in {} (with {})", player.getName(), pos.getX(), pos.getY(), pos.getZ(), targetDimension.dimension().location(), item);
        } catch (NullPointerException e) {
			Lulasmod.LOGGER.error("Couldn't find the specified dimension");
		}
	}

    public static ItemStack getInventoryStack(Player player, Item item) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack.getItem().equals(item)) {
                return itemStack;
            }
        }
        return null;
    }

	public static void pocketTeleport(Entity victim) {
        victim.teleportTo(
            Objects.requireNonNull(victim.getServer()).getLevel((
			    victim.level().dimension().equals(ModDimensions.POCKET_DIMENSION)?
                    Level.OVERWORLD :
                    ModDimensions.POCKET_DIMENSION)),
            victim.getX(), victim.getY(), victim.getZ(), Set.of(), victim.getYRot(), victim.getXRot(), false);
    }

	public static <T> LinkedList<T> makeMutable(List<T> immutable) {
		return immutable == null? new LinkedList<>() : new LinkedList<>(immutable);
	}
}