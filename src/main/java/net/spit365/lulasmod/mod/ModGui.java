package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.util.SpellHotbar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ModGui {
	private static final Identifier SPELL_HOTBAR_TEXTURE = Identifier.of(Lulasmod.MOD_ID, "textures/gui/spell_hotbar.png");
	public static List<ItemStack> SPELL_HOTBAR_LIST = new LinkedList<>();
	public static void init(){
		HudElementRegistry.addFirst(Identifier.of(Lulasmod.MOD_ID, "spell_hotbar"), (context, renderTickCounter) -> {
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (player == null) return;
			if (Arrays.stream(Hand.values()).anyMatch(hand -> player.getStackInHand(hand).getItem() instanceof SpellHotbar)) {
				int x = context.getScaledWindowWidth() / 2 - 121;
				int y = context.getScaledWindowHeight() - 42;
				if (player.getMainHandStack().getItem() instanceof SpellHotbar) x += (player.getInventory().getSelectedSlot() * 20) + 29;
				context.drawTexture(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, SPELL_HOTBAR_TEXTURE, x, y - 44, 0, 0, 24, 64, 24, 64);
				for (int i = 0; i < Math.min(SPELL_HOTBAR_LIST.size(), 3); i++) context.drawItem(SPELL_HOTBAR_LIST.get(i), x + 4, y + (i * -20));
			}
		});
	}
}
