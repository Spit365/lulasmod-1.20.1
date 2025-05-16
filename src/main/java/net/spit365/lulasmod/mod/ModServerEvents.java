package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.item.seal.SealItem;
import net.spit365.lulasmod.tag.TagManager;

public class ModServerEvents extends ModMethods.ServerUpdates{
    @SuppressWarnings("deprecation")
    public static void init(){
        ServerTickCallback.EVENT.register(minecraftServer -> {
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()){
                ModMethods.ServerUpdates.repelMiner(player);
                ModMethods.ServerUpdates.updateSpells(player);
            }
            ModMethods.ServerUpdates.updateSpores(minecraftServer);
            ModMethods.ServerUpdates.updateImpaled();
        });
        ServerPlayNetworking.registerGlobalReceiver(Mod.Packets.CYCLE_PLAYER_SPELL, (a, player, b, c, d) -> {
            if (player.getMainHandStack().getItem() instanceof SpellHotbar item) item.cycle(player);
            else if (player.getOffHandStack().getItem() instanceof SpellHotbar item) item.cycle(player);
        });
    }
}
