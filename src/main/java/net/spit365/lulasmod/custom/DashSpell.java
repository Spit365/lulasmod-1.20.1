package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.packet.DashSpellUsagesS2CPacket;

public final class DashSpell {
	public static int usages;

	public static void tick(ServerPlayerEntity player) {
		Integer i = player.getAttached(ModData.DASH_SPELL);
		ServerPlayNetworking.send(player, new DashSpellUsagesS2CPacket(i == null ? -1 : i));
	}

	public static int onUse(ServerWorld world, PlayerEntity player, int cooldownDivisor) {
        if (player.hasStatusEffect(StatusEffects.SLOWNESS)) return SealItem.FAIL_RESULT;

        int maxUsages = 5 * cooldownDivisor;
        Integer usages = player.getAttached(ModData.DASH_SPELL);
        if (usages == null) usages = maxUsages;
        usages--;
        int cooldown = usages > 0 ? 5 : (player.isOnGround() ? 20 : 40);

		if (dash(world, player)) return SealItem.FAIL_RESULT;

		player.setAttached(ModData.DASH_SPELL, usages <= 0 ? maxUsages : Math.min(maxUsages, usages));
        return cooldown;
    }

	public static boolean dash(ServerWorld world, PlayerEntity player) {
		PlayerInput input = ((ServerPlayerEntity) player).getPlayerInput();
		Vec3d movementDirection = new Vec3d(
			mapMovement(input.left(), input.right()),
			mapMovement(input.jump(), input.sneak()),
			mapMovement(input.forward(), input.backward())
		).rotateY((float) -Math.toRadians(player.getYaw()));
		if (movementDirection.lengthSquared() < 1E-10F) return false;

		player.addVelocity(movementDirection.normalize().add(0, 0.25, 0));
		player.velocityModified = true;
		player.fallDistance = 0;

		world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
		return true;
	}

	private static int mapMovement(boolean direction1, boolean direction2){
		return direction1 == direction2 ? 0 : (direction1 ? 1 : -1);
	}
}

