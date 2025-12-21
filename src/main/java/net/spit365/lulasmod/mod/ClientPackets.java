package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.LinkedLightning;
import net.spit365.lulasmod.custom.TimeForward;
import net.spit365.lulasmod.packet.*;
import net.spit365.lulasmod.renderer.DashSpellUsagesRenderer;

public class ClientPackets {
    public static void init(){
		ClientPlayNetworking.registerGlobalReceiver(DashSpellUsagesS2CPacket.ID, (dashSpellUsagesS2CPacket, context) -> DashSpellUsagesRenderer.usages = dashSpellUsagesS2CPacket.usages());
		ClientPlayNetworking.registerGlobalReceiver(BleedProgressS2CPacket.ID, (bleedProgressS2CPacket, context) -> Bleed.progress = bleedProgressS2CPacket.progress());
		ClientPlayNetworking.registerGlobalReceiver(SummonBleedS2CPacket.ID, (summonBleedS2CPacket, context) -> Bleed.summonParticles(summonBleedS2CPacket.getPos(), context.client().world));
		ClientPlayNetworking.registerGlobalReceiver(SpellHotbarListS2CPacket.ID, (spellHotbarListS2CPacket, context) -> ModGui.SPELL_HOTBAR_LIST = spellHotbarListS2CPacket.list());
		ClientPlayNetworking.registerGlobalReceiver(TimeForwardAnimationS2CPacket.ID, (cycleSpellHotbarC2SPacket, context) -> TimeForward.Animator.start(context.client().world));
        ClientPlayNetworking.registerGlobalReceiver(LightningLinkS2CPacket.ID, (payload, context) -> {
            LinkedLightning.Render.linkedLightnings.clear();
            LinkedLightning.Render.linkedLightnings.addAll(payload.linkedLightning());
        });
	}
}
