package net.spit365.lulasmod.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModSpells;
import net.spit365.lulasmod.packet.DashSpellUsagesS2CPacket;
import net.spit365.lulasmod.renderer.SpellHotbarRenderer;

public final class DashSpell {
	public static int usages;

	public static void tick(ServerPlayer player) {
        ServerPlayNetworking.send(player,
			new DashSpellUsagesS2CPacket(player.getAttachedOrElse(ModData.DASH_SPELL, -1)));

		Integer cooldown = player.getAttached(ModData.DASH_COOLDOWN);
		if (cooldown != null) {
			if (cooldown <= 0) player.removeAttached(ModData.DASH_COOLDOWN);
			else player.setAttached(ModData.DASH_COOLDOWN, cooldown - 1);
		}
	}

	public static int onUse(ServerLevel world, Player player, int cooldownDivisor) {
        if (player.hasEffect(MobEffects.SLOWNESS)) return SealItem.FAIL_RESULT;

        int maxUsages = 5 * cooldownDivisor;
        int usages = player.getAttachedOrElse(ModData.DASH_SPELL, maxUsages) - 1;
        int cooldown = usages > 0 ? 5 : (player.onGround() ? 20 : 40);

		if (!dashInternal(world, player)) return SealItem.FAIL_RESULT;

		player.setAttached(ModData.DASH_SPELL, usages <= 0 ? maxUsages : Math.min(maxUsages, usages));
        return cooldown;
    }

	public static void dash(ServerLevel world, Player player) {
		Integer prevCooldown = player.getAttached(ModData.DASH_COOLDOWN);
		if (prevCooldown != null && prevCooldown > 0) return;

		if (player.hasEffect(MobEffects.SLOWNESS)) return;

		int maxUsages = 5;
        int usages = player.getAttachedOrElse(ModData.DASH_SPELL, maxUsages) - 1;
        int cooldown = usages > 0 ? 10 : (player.onGround() ? 20 : 40);

		if (!dashInternal(world, player)) return;

		player.setAttached(ModData.DASH_SPELL, usages <= 0 ? maxUsages : Math.min(maxUsages, usages));
        player.setAttached(ModData.DASH_COOLDOWN, cooldown);
    }

	private static boolean dashInternal(ServerLevel world, Player player) {
		Input input = ((ServerPlayer) player).getLastClientInput();
		Vec3 movementDirection = new Vec3(
			mapMovement(input.left(), input.right()),
			mapMovement(input.jump(), input.shift()),
			mapMovement(input.forward(), input.backward())
		).yRot((float) -Math.toRadians(player.getYRot()));
		if (movementDirection.lengthSqr() < 1E-10F) return false;

		player.push(movementDirection.normalize().add(0, 0.25, 0));
		player.hurtMarked = true;
		player.fallDistance = 0;

		world.sendParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
		return true;
	}

	private static int mapMovement(boolean direction1, boolean direction2) {
		return direction1 == direction2 ? 0 : (direction1 ? 1 : -1);
	}

	@Environment(EnvType.CLIENT)
	public static Integer showUsages(LocalPlayer player) {
		if (player == null) return null;

		if (player.getAttached(ModData.APPLIED_SHIMMER_VARIANT) == Shimmer.Variant.PACE) return 5;

		SealItem sealItem = null;
		for (InteractionHand hand : InteractionHand.values()) {
			if (player.getItemInHand(hand).getItem() instanceof SealItem item) {
				sealItem = item;
				break;
			}
		}
		if (sealItem != null && !SpellHotbarRenderer.spellHotbarList.isEmpty() && SpellHotbarRenderer.spellHotbarList.getFirst().is(ModSpells.DASH_SPELL)) return Math.max(sealItem.cooldownDivisor * 5, 0);
        return null;
    }
}

