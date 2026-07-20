package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModSpells;
import com.mojang.blaze3d.platform.Window;
import java.util.Arrays;

public final class KinesisInteractionRenderer {
	public static final double INTERACTION_RANGE_RADIANS = Math.toRadians(20);
	private static final double tanTheta = Math.tan(INTERACTION_RANGE_RADIANS);
	private static final int COLOR = 0xFF50C8FF; //argb
	private static double lastFOV;

	@Environment(EnvType.CLIENT)
	public static void render(GuiGraphics context, DeltaTracker renderTickCounter) {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
		if (player == null || Arrays.stream(InteractionHand.values()).noneMatch(hand -> player.getItemInHand(hand).getItem() instanceof SealItem) || SpellHotbarRenderer.spellHotbarList.isEmpty() || !SpellHotbarRenderer.spellHotbarList.getFirst().is(ModSpells.KINESIS_SORCERY)) return;
		Window win = mc.getWindow();
		int w = win.getGuiScaledWidth();
		int h = win.getGuiScaledHeight();
		float cx = w * 0.5f;
		float cy = h * 0.5f;

		Options options = mc.options;
		double newFOV = Math.toRadians(options.fov().get()) * player.getFieldOfViewModifier(options.getCameraType().equals(CameraType.FIRST_PERSON), 1f);
		double lerpedFOV = lastFOV == 0L ? newFOV : Mth.lerp(renderTickCounter.getGameTimeDeltaPartialTick(true), lastFOV, newFOV);
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
