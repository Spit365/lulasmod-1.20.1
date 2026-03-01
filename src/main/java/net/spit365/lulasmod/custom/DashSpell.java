package net.spit365.lulasmod.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModSpells;
import net.spit365.lulasmod.packet.DashSpellUsagesS2CPacket;
import net.spit365.lulasmod.renderer.SpellHotbarRenderer;

public final class DashSpell {
	public static int usages;

	public static void tick(ServerPlayerEntity player) {
		Integer timesLeft = player.getAttached(ModData.DASH_SPELL);
		ServerPlayNetworking.send(player, new DashSpellUsagesS2CPacket(timesLeft == null ? -1 : timesLeft));

		Integer cooldown = player.getAttached(ModData.DASH_COOLDOWN);
		if (cooldown != null) {
			if (cooldown <= 0) player.removeAttached(ModData.DASH_COOLDOWN);
			else player.setAttached(ModData.DASH_COOLDOWN, cooldown - 1);
		}
	}

	public static int onUse(ServerWorld world, PlayerEntity player, int cooldownDivisor) {
        if (player.hasStatusEffect(StatusEffects.SLOWNESS)) return SealItem.FAIL_RESULT;

        int maxUsages = 5 * cooldownDivisor;
        Integer usages = player.getAttached(ModData.DASH_SPELL);
        if (usages == null) usages = maxUsages;
        usages--;
        int cooldown = usages > 0 ? 5 : (player.isOnGround() ? 20 : 40);

		if (!dashInternal(world, player)) return SealItem.FAIL_RESULT;

		player.setAttached(ModData.DASH_SPELL, usages <= 0 ? maxUsages : Math.min(maxUsages, usages));
        return cooldown;
    }

	public static void dash(ServerWorld world, PlayerEntity player) {

		Integer prevCooldown = player.getAttached(ModData.DASH_COOLDOWN);
		if (prevCooldown != null && prevCooldown > 0) return;

		if (player.hasStatusEffect(StatusEffects.SLOWNESS)) return;

		int maxUsages = 5;
        Integer usages = player.getAttached(ModData.DASH_SPELL);
        if (usages == null) usages = maxUsages;
        usages--;
        int cooldown = usages > 0 ? 10 : (player.isOnGround() ? 20 : 40);

		if (!dashInternal(world, player)) return;

		player.setAttached(ModData.DASH_SPELL, usages <= 0 ? maxUsages : Math.min(maxUsages, usages));
        player.setAttached(ModData.DASH_COOLDOWN, cooldown);
    }

	private static boolean dashInternal(ServerWorld world, PlayerEntity player) {
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

	@Environment(EnvType.CLIENT)
	public static Integer showUsages(ClientPlayerEntity player) {
		if (player == null) return null;

		if (player.getAttached(ModData.APPLIED_SHIMMER_VARIANT) == Shimmer.Variant.PACE) return 5;

		SealItem sealItem = null;
		for (Hand hand : Hand.values()) {
			if (player.getStackInHand(hand).getItem() instanceof SealItem item) {
				sealItem = item;
				break;
			}
		}
		if (sealItem != null && !SpellHotbarRenderer.spellHotbarList.isEmpty() && SpellHotbarRenderer.spellHotbarList.getFirst().isOf(ModSpells.DASH_SPELL)) return Math.max(sealItem.cooldownDivisor * 5, 0);
        return null;
    }
}

