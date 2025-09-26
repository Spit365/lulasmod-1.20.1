package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.spit365.lulasmod.manager.TimeForwardAnimator;

public class ModClientTick {
	public static TimeForwardAnimator timeForwardAnimator;

	public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeybinds.CYCLE_SPELL_KEY.wasPressed() && client.player != null) ClientPlayNetworking.send(new ModPackets.CycleSpellHotbarC2SPacket());
            ClientWorld world = client.world;
			if (timeForwardAnimator == null && world != null) timeForwardAnimator = new TimeForwardAnimator(world);
			if (timeForwardAnimator != null) timeForwardAnimator.tick();

        });
    }
}