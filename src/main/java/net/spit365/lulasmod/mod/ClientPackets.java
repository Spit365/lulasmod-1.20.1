package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.DashSpell;
import net.spit365.lulasmod.custom.LinkedLightning;
import net.spit365.lulasmod.custom.TimeForward;
import net.spit365.lulasmod.packet.*;
import net.spit365.lulasmod.renderer.SpellHotbarRenderer;

public class ClientPackets {
    public static void init(){
		ClientPlayNetworking.registerGlobalReceiver(BleedProgressS2CPacket.ID, (bleedProgressS2CPacket, context) -> Bleed.progress = bleedProgressS2CPacket.progress());
		ClientPlayNetworking.registerGlobalReceiver(DashSpellUsagesS2CPacket.ID, (dashSpellUsagesS2CPacket, context) -> DashSpell.usages = dashSpellUsagesS2CPacket.usages());
        ClientPlayNetworking.registerGlobalReceiver(LightningLinkS2CPacket.ID, (lightningLinkS2CPacket, context) -> LinkedLightning.Render.linkedLightnings = lightningLinkS2CPacket.linkedLightning());
		ClientPlayNetworking.registerGlobalReceiver(SetTimeForwardAnimationStateS2CPacket.ID, (setTimeForwardAnimationStateS2CPacket, context) -> {
			if (setTimeForwardAnimationStateS2CPacket.state()) TimeForward.Animator.start(context.client().world);
			else TimeForward.Animator.stop();
		});
		ClientPlayNetworking.registerGlobalReceiver(SpellHotbarListS2CPacket.ID, (spellHotbarListS2CPacket, context) -> SpellHotbarRenderer.spellHotbarList = spellHotbarListS2CPacket.list());
		ClientPlayNetworking.registerGlobalReceiver(SummonBleedS2CPacket.ID, (summonBleedS2CPacket, context) -> Bleed.summonParticles(summonBleedS2CPacket.getPos(), context.client().world));
	}
}
