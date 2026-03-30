package net.spit365.lulasmod.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ModUtil {
    public static @Nullable Entity selectClosestEntity(Entity selector, double radius) {
        Vec3d selectionCenter = selector.getRotationVec(1).normalize().multiply(radius).add(selector.getPos());
        Entity selectedEntity = null;
        for (Entity entityInRange : selector.getWorld().getOtherEntities(selector, new Box(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))){
            if (selectedEntity == null || selectedEntity.getPos().squaredDistanceTo(selector.getPos()) > entityInRange.getPos().squaredDistanceTo(selector.getPos()))
                selectedEntity = entityInRange;
        }
        return selectedEntity;
    }

	public static void sendHome(PlayerEntity player, Item item){
		try {
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
			MinecraftServer server = Objects.requireNonNull(player.getServer());
			ServerPlayerEntity.Respawn respawn = serverPlayer.getRespawn();
			ServerWorld targetDimension = Objects.requireNonNull(server.getWorld(respawn != null ? respawn.dimension() : World.OVERWORLD));
			BlockPos pos = respawn != null ? respawn.pos() : targetDimension.getSpawnPos();
			if (player.teleport(targetDimension, pos.getX(), pos.getY(), pos.getZ(), Set.of(), player.getYaw(), player.getPitch(), true))
				Lulasmod.LOGGER.info("{} was sent home to {} {} {} in {} (with {})", player.getName(), pos.getX(), pos.getY(), pos.getZ(), targetDimension.getRegistryKey().getValue(), item);
        } catch (NullPointerException e) {
			Lulasmod.LOGGER.error("Couldn't find the specified dimension");
		}
	}

    public static ItemStack getInventoryStack(PlayerEntity player, Item item){
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.getItem().equals(item)) {
                return itemStack;
            }
        }
        return null;
    }

	public static void pocketTeleport(Entity victim) {
        victim.teleport(
            Objects.requireNonNull(victim.getServer()).getWorld((
			    victim.getWorld().getRegistryKey().equals(ModDimensions.POCKET_DIMENSION)?
                    World.OVERWORLD :
                    ModDimensions.POCKET_DIMENSION)),
            victim.getX(), victim.getY(), victim.getZ(), Set.of(), victim.getYaw(), victim.getPitch(), false);
    }

	public static <T> LinkedList<T> makeMutable(List<T> immutable){
		return immutable == null? new LinkedList<>() : new LinkedList<>(immutable);
	}
}