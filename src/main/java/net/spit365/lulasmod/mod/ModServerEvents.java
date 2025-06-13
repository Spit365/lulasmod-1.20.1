package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.manager.TickerManager;

public class ModServerEvents {
    @SuppressWarnings("deprecation")
    public static void init(){
        ServerTickCallback.EVENT.register(TickerManager::tickAll);
        ServerPlayNetworking.registerGlobalReceiver(ModServer.Packets.CYCLE_PLAYER_SPELL, (a, player, b, c, d) -> {
            if (player.getMainHandStack().getItem() instanceof SpellHotbar item) item.cycleList(player);
            else if (player.getOffHandStack().getItem() instanceof SpellHotbar item) item.cycleList(player);
        });
    }
}