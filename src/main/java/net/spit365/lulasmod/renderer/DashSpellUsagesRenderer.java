package net.spit365.lulasmod.renderer;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModSpells;
import net.spit365.lulasmod.packet.DashSpellUsagesS2CPacket;

public class DashSpellUsagesRenderer {
	public static int usages;

	public static void tick(ServerPlayerEntity player) {
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
		if (sealItem == null || SpellHotbarRenderer.SPELL_HOTBAR_LIST.isEmpty() || !SpellHotbarRenderer.SPELL_HOTBAR_LIST.getFirst().isOf(ModSpells.DASH_SPELL)) return;

		int x = context.getScaledWindowWidth() / 2 - 5;
		int y = context.getScaledWindowHeight() / 2 - 3;

		int maxUsages = Math.max(sealItem.cooldownDivisor * 5, 0);
		if (usages < 0)  usages = maxUsages;
		int color = usages != 1 ? Colors.WHITE : (player.isOnGround() ? Colors.YELLOW : Colors.RED);
		context.drawText(client.textRenderer, String.valueOf(usages), x - 11, y, color, true);
		context.drawText(client.textRenderer, String.valueOf(maxUsages), x + 15, y, color, true);
	}
}

