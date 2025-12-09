package net.spit365.lulasmod.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModGui;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.Arrays;

public class KinesisInteractionRenderer {
	public static final double INTERACTION_RANGE_RADIANS = Math.toRadians(20);
	private static final double tanTheta = Math.tan(INTERACTION_RANGE_RADIANS);
	private static final int COLOR = 0xFF50C8FF; //argb
	private static double lastFOV;

	public static void render(DrawContext context, RenderTickCounter renderTickCounter) {
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayerEntity player = mc.player;
		if (player == null || Arrays.stream(Hand.values()).noneMatch(hand -> player.getStackInHand(hand).getItem() instanceof SealItem) || ModGui.SPELL_HOTBAR_LIST.isEmpty() || !ModGui.SPELL_HOTBAR_LIST.getFirst().isOf(ModSpells.KINESIS_SORCERY)) return;
		Window win = mc.getWindow();
		int w = win.getScaledWidth();
		int h = win.getScaledHeight();
		float cx = w * 0.5f;
		float cy = h * 0.5f;

		GameOptions options = mc.options;
		double newFOV = Math.toRadians(options.getFov().getValue()) * player.getFovMultiplier(options.getPerspective().equals(Perspective.FIRST_PERSON), 1f);
		double lerpedFOV = lastFOV == 0L ? newFOV : MathHelper.lerp(renderTickCounter.getTickProgress(true), lastFOV, newFOV);
		lastFOV = lerpedFOV;

		double tanLerpedFOV = Math.tan(lerpedFOV * 0.5d);
		float ry = (float) (cy * (tanTheta / tanLerpedFOV));
		float rx = (float) (cx * (tanTheta / (tanLerpedFOV * ((double) w / h))));
		double p = Math.PI * (3d * (rx + ry) - Math.sqrt((3d * rx + ry) * (rx + 3d * ry))); //Ramanujan-Approximation
		int segments = (int) Math.ceil(p / 1.25d);

		int halfThickness = 1;
		for (int i = 0; i < segments; i++) {
			double ang = (Math.PI * 2d) * (i / (double) segments);
			int x = Math.round(cx + (float) (rx * Math.cos(ang)));
			int y = Math.round(cy + (float) (ry * Math.sin(ang)));

			context.fill(
				x - halfThickness, y - halfThickness,
				x + halfThickness, y + halfThickness,
				COLOR
			);
		}
	}
}
