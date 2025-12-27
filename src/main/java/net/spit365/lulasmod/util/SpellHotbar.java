package net.spit365.lulasmod.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.packet.SpellHotbarListS2CPacket;

import java.util.List;

public interface SpellHotbar {
    List<Identifier> getHotbarList(PlayerEntity player);
    void onCycle(PlayerEntity player, int value);

    static void tick(ServerPlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof SpellHotbar item)
            sendSpellListPacket(player, item.getHotbarList(player));
        else if (player.getOffHandStack().getItem() instanceof SpellHotbar item)
            sendSpellListPacket(player, item.getHotbarList(player));
    }

    static void sendSpellListPacket(ServerPlayerEntity player, List<Identifier> list) {
        if (list != null) ServerPlayNetworking.send(player, new SpellHotbarListS2CPacket(list.stream().map(id -> new ItemStack(Registries.ITEM.get(id))).toList()));
    }
}