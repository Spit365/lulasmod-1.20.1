package net.spit365.lulasmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModDimensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

	public static void sendHome(Player player, @Nullable String reason) {
		try {
            if (!(player instanceof ServerPlayer serverPlayer)) return;
			MinecraftServer server = Objects.requireNonNull(player.level().getServer());
			LevelData.RespawnData respawnData = Optional.ofNullable(serverPlayer.getRespawnConfig()).map(ServerPlayer.RespawnConfig::respawnData).orElse(server.getRespawnData());
			ResourceKey<Level> dimension = respawnData.dimension();
			BlockPos pos = respawnData.pos();
			if (
				player.teleportTo(
					Objects.requireNonNull(server.getLevel(dimension)),
					pos.getX(),
					pos.getY(),
					pos.getZ(),
					Set.of(),
					player.getYRot(),
					player.getXRot(),
					true
				)
			) Lulasmod.LOGGER.info(
				"{} was sent home to {} {} {} in {}{}",
				player.getName(),
				pos.getX(),
				pos.getY(),
				pos.getZ(),
				dimension.identifier(),
				reason != null ? " (with " + reason + ")" : ""
			);
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
		Level level = victim.level();
		victim.teleportTo(
	        Optional.ofNullable(level.getServer()).map(server -> server.getLevel(
				level.dimension().equals(ModDimensions.POCKET_DIMENSION) ?
					Level.OVERWORLD :
					ModDimensions.POCKET_DIMENSION
	        )).orElseThrow(),
	        victim.getX(),
	        victim.getY(),
	        victim.getZ(),
	        Set.of(),
	        victim.getYRot(),
	        victim.getXRot(),
	        false
        );
    }

	public static <T> LinkedList<T> makeMutable(List<T> immutable) {
		return immutable == null? new LinkedList<>() : new LinkedList<>(immutable);
	}
}