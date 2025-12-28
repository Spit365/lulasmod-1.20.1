package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModMethods {

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
			if (player instanceof ServerPlayerEntity serverPlayer) {
				MinecraftServer server = Objects.requireNonNull(player.getServer());
				ServerPlayerEntity.Respawn respawn = serverPlayer.getRespawn();
				BlockPos pos;
				ServerWorld targetDimension;
				if (respawn != null) {
					targetDimension = Objects.requireNonNull(server.getWorld(respawn.dimension()));
					pos = respawn.pos();
				} else {
					targetDimension = Objects.requireNonNull(server.getWorld(World.OVERWORLD));
					pos = targetDimension.getSpawnPos();
				}
				if (player.teleport(targetDimension, pos.getX(), pos.getY(), pos.getZ(), Set.of(), player.getYaw(), player.getPitch(), true))
					Lulasmod.LOGGER.info("{} was sent home to {} {} {} in {} (with {})", player.getName(), pos.getX(), pos.getY(), pos.getZ(), targetDimension.getRegistryKey().getValue(), item);
			}
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
        if (!victim.teleport(
		   Objects.requireNonNull(victim.getServer()).getWorld((
			   victim.getWorld().getRegistryKey().equals(ModDimensions.POCKET_DIMENSION)?
                        World.OVERWORLD :
                        ModDimensions.POCKET_DIMENSION))
		   , victim.getX(), victim.getY(), victim.getZ(), EnumSet.noneOf(PositionFlag.class), victim.getYaw(), victim.getPitch(), false))
            Lulasmod.LOGGER.error("Could not perform teleport. Registry key: {}, Entity: {}", ModDimensions.POCKET_DIMENSION, victim);
    }

	public static @NotNull Hand getHandFromStack(LivingEntity user, ItemStack stack) {
		return Arrays.stream(Hand.values()).filter(hand -> user.getStackInHand(hand).equals(stack)).findFirst().orElse(Hand.MAIN_HAND);
	}

    public static <T> LinkedList<T> makeMutable(List<T> immutable){
		return immutable == null? new LinkedList<>() : new LinkedList<>(immutable);
	}

    public static void outlineBox(Box box, ServerWorld world, SimpleParticleType particle){
        final Vec3d start = box.getCenter().add(box.getLengthX() / -2, box.getLengthY() / -2, box.getLengthZ() / -2);
        final Vec3d end = box.getCenter().add(box.getLengthX() / 2, box.getLengthY() / 2, box.getLengthZ() / 2);

        for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, start.getY(), start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, start.getY(), end.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, end.getY(), start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, end.getY(), end.getZ(), 0, 0, 0, 0, 0);

        for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);

        for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, start.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, end.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
    }
}