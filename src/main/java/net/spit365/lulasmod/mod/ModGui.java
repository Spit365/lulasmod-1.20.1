package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.renderer.DashSpellRenderer;
import net.spit365.lulasmod.renderer.KinesisInteractionRenderer;
import net.spit365.lulasmod.renderer.SpellHotbarRenderer;
import net.spit365.lulasmod.util.ClientRegisterHelper;

@Environment(EnvType.CLIENT)
public final class ModGui {
	public static void init() {
        ClientRegisterHelper.hudElement("spell_hotbar", (context, renderTickCounter) -> SpellHotbarRenderer.render(context));
        ClientRegisterHelper.hudElement("kinesis_interaction_range", KinesisInteractionRenderer::render);
        ClientRegisterHelper.hudElement("purloining_usages", (context, renderTickCounter) -> DashSpellRenderer.render(context));
        ClientRegisterHelper.hudElement("bleed_progress", (context, renderTickCounter) -> Bleed.render(context));
	}
}
