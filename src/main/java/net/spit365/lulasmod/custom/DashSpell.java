package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
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
	public static final Set<PlayerEntity> DASH_IMPACT_SET = new HashSet<>();

	public static void tick(ServerPlayerEntity player) {
		if (DASH_IMPACT_SET.contains(player) && player.getWorld() instanceof ServerWorld world && (player.verticalCollision || player.horizontalCollision)){
			DASH_IMPACT_SET.remove(player);
			Vec3d pos = player.getPos();
			world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1, 1);
			world.spawnParticles(ModParticles.EXPLOSION, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0);
			for (Entity target : world.getOtherEntities(player, new Box(pos.add(2), pos.add(-2))))
				target.damage(world, world.getDamageSources().explosion(player, player), 2);
		}
		Integer i = player.getAttached(ModData.DASH_SPELL);
		ServerPlayNetworking.send(player, new DashSpellUsagesS2CPacket(i == null ? -1 : i));
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
		if (usages < 0)  usages = maxUsages;
		int color = usages != 1 ? Colors.WHITE : (player.isOnGround() ? Colors.YELLOW : Colors.RED);
		context.drawText(client.textRenderer, String.valueOf(usages), x - 11, y, color, true);
		context.drawText(client.textRenderer, String.valueOf(maxUsages), x + 15, y, color, true);
	}
}

