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
    private static double lastFOV = 360;
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
        HudElementRegistry.addLast(Identifier.of(Lulasmod.MOD_ID, "kinesis_interaction_range"), (context, renderTickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            ClientPlayerEntity player = mc.player;
            if (player == null) return;
			if (Arrays.stream(Hand.values()).anyMatch(hand -> player.getStackInHand(hand).getItem() instanceof SpellHotbar) && !SPELL_HOTBAR_LIST.isEmpty() && SPELL_HOTBAR_LIST.getFirst().isOf(ModSpells.KINESIS_SORCERY)) {

                Window win = mc.getWindow();

                int w = win.getScaledWidth();
                int h = win.getScaledHeight();
                float cx = w * 0.5f;
                float cy = h * 0.5f;

                GameOptions options = mc.options;
                double vFov = Math.toRadians(options.getFov().getValue()) * player.getFovMultiplier(options.getPerspective().equals(Perspective.FIRST_PERSON), 1f);
                double lerpedFOV = MathHelper.lerp(renderTickCounter.getTickProgress(true), lastFOV, vFov);
                lastFOV = vFov;

                double theta = Math.toRadians(20.0);
                float ry = (float) (cy * (Math.tan(theta) / Math.tan(lerpedFOV * 0.5)));
                float rx = (float) (cx * (Math.tan(theta) / Math.tan(2.0 * Math.atan(((double) w / h) * Math.tan(lerpedFOV * 0.5)) * 0.5)));
                double p = Math.PI * (3.0 * (rx + ry) - Math.sqrt((3.0 * rx + ry) * (rx + 3.0 * ry)));
                double spacingPx = 1.25;
                int seg = (int) Math.ceil(p / spacingPx);
                final int segments = MathHelper.clamp(seg, 48, 720);

                int color = 0xFF50C8FF; //argb

                final int thickness = 2;
                int half = Math.max(0, thickness / 2);

                for (int i = 0; i < segments; i++) {
                    double ang = (Math.PI * 2.0) * (i / (double) segments);
                    float x = cx + (float) (rx * Math.cos(ang));
                    float y = cy + (float) (ry * Math.sin(ang));
                    int ix = MathHelper.floor(x);
                    int iy = MathHelper.floor(y);

                    context.fill(
                        ix - half, iy - half,
                        ix + (thickness - half), iy + (thickness - half),
                        color);
                }
            } else lastFOV = 360;
		});
	}
}
