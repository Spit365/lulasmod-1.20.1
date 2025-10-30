package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.manager.TickerManager;

public class ModServerEvents {
    public static void init(){
        ServerTickEvents.END_WORLD_TICK.register(TickerManager::tickAll);
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.CYCLE_PLAYER_SPELL_C2S.ID, (payload, context) -> {
            if (context.player().getMainHandStack().getItem() instanceof SpellHotbar item) item.cycleList(context.player());
            else if (context.player().getOffHandStack().getItem() instanceof SpellHotbar item) item.cycleList(context.player());
        });
    }
}