package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.spit365.clienttweaks.util.ModUtil;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.DashSpell;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.packet.*;
import net.spit365.lulasmod.renderer.LinkedLightningRender;
import net.spit365.lulasmod.renderer.SpellHotbarRenderer;
import net.spit365.lulasmod.renderer.TimeForwardRenderer;

@Environment(EnvType.CLIENT)
public final class ClientPackets {
    public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(BleedProgressS2CPacket.ID, (bleedProgressS2CPacket, context) -> Bleed.progress = bleedProgressS2CPacket.progress());
		ClientPlayNetworking.registerGlobalReceiver(DashSpellUsagesS2CPacket.ID, (dashSpellUsagesS2CPacket, context) -> DashSpell.usages = dashSpellUsagesS2CPacket.usages());
        ClientPlayNetworking.registerGlobalReceiver(LightningLinkS2CPacket.ID, (lightningLinkS2CPacket, context) -> LinkedLightningRender.linkedLightnings = lightningLinkS2CPacket.linkedLightning());
		ClientPlayNetworking.registerGlobalReceiver(SetTimeForwardAnimationStateS2CPacket.ID, (setTimeForwardAnimationStateS2CPacket, context) -> {
			if (setTimeForwardAnimationStateS2CPacket.state()) TimeForwardRenderer.start(context.client().level);
			else TimeForwardRenderer.stop();
		});
		ClientPlayNetworking.registerGlobalReceiver(SpellHotbarListS2CPacket.ID, (spellHotbarListS2CPacket, context) -> SpellHotbarRenderer.spellHotbarList = spellHotbarListS2CPacket.list());
		ClientPlayNetworking.registerGlobalReceiver(SummonBleedS2CPacket.ID, (summonBleedS2CPacket, context) -> ModUtil.summonBleed(summonBleedS2CPacket.pos(), context.client().level));
		ClientPlayNetworking.registerGlobalReceiver(MindShimmerS2CPacket.ID, (mindShimmerS2CPacket, context) -> Shimmer.mindShimmerEnabled = mindShimmerS2CPacket.state());
	}
}
