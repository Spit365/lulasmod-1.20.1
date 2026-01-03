package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.mod.ModSpells;
import net.spit365.lulasmod.packet.DashSpellUsagesS2CPacket;
import net.spit365.lulasmod.renderer.SpellHotbarRenderer;

import java.util.HashSet;
import java.util.Set;

public class DashSpell {
	public static int usages;

	public static void tick(ServerPlayerEntity player) {
		Integer i = player.getAttached(ModData.DASH_SPELL);
		ServerPlayNetworking.send(player, new DashSpellUsagesS2CPacket(i == null ? -1 : i));
	}

	public static int onUse(ServerWorld world, PlayerEntity player, float efficiencyMultiplier, int cooldownDivisor) {
        if (player.hasStatusEffect(StatusEffects.SLOWNESS)) return SealItem.FAIL_RESULT;

        int maxUsages = 5 * cooldownDivisor;
        Integer usages = player.getAttached(ModData.DASH_SPELL);
        if (usages == null) usages = maxUsages;
        usages--;
        int cooldown = usages <= 0 ? (player.isOnGround() ? 20 : 40) : 5;

		PlayerInput input = ((ServerPlayerEntity) player).getPlayerInput();
		Vec3d movementDirection = new Vec3d(
			mapMovement(input.left(), input.right()),
			mapMovement(input.jump(), input.sneak()),
			mapMovement(input.forward(), input.backward())
		).rotateY((float) -Math.toRadians(player.getYaw()));
		if (movementDirection.lengthSquared() < 1E-10F) return SealItem.FAIL_RESULT;

        player.setAttached(ModData.DASH_SPELL, usages <= 0 ? maxUsages : Math.min(maxUsages, usages));
		player.addVelocity(movementDirection.normalize().add(0, 0.25, 0));
        player.velocityModified = true;
        player.fallDistance = 0;
        world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
        return cooldown;
    }

	private static int mapMovement(boolean direction1, boolean direction2){
		return direction1 == direction2 ? 0 : (direction1 ? 1 : -1);
	}

	public static void render(DrawContext context){
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity player = client.player;
		if (player == null) return;
		SealItem sealItem = null;
		for (Hand hand : Hand.values()) {
			if (player.getStackInHand(hand).getItem() instanceof SealItem item) {
				sealItem = item;
				break;
			}
		}
		if (sealItem == null || SpellHotbarRenderer.spellHotbarList.isEmpty() || !SpellHotbarRenderer.spellHotbarList.getFirst().isOf(ModSpells.DASH_SPELL)) return;

		int x = context.getScaledWindowWidth() / 2 - 5;
		int y = context.getScaledWindowHeight() / 2 - 3;

		int maxUsages = Math.max(sealItem.cooldownDivisor * 5, 0);
		if (usages <= 0)  usages = maxUsages;
		int color = usages != 1 ? Colors.WHITE : (player.isOnGround() ? Colors.YELLOW : Colors.RED);
		context.drawText(client.textRenderer, String.valueOf(usages), x - 11, y, color, true);
		context.drawText(client.textRenderer, String.valueOf(maxUsages), x + 15, y, color, true);
	}
}

