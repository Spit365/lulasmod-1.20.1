package net.spit365.lulasmod.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.packet.SpellHotbarListS2CPacket;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface SpellHotbar {
    List<Identifier> getHotbarList(PlayerEntity player);
    void onCycle(PlayerEntity player, Function<List<Identifier>, List<Identifier>> cycleFunction);

    static void tick(ServerPlayerEntity player) {
        for (Hand hand : Hand.values()) {
            if (!(player.getStackInHand(hand).getItem() instanceof SpellHotbar item)) continue;
            List<Identifier> hotbarList = item.getHotbarList(player);
            if (hotbarList != null) ServerPlayNetworking.send(player, new SpellHotbarListS2CPacket(hotbarList.stream().map(id -> new ItemStack(Registries.ITEM.get(id))).toList()));
            break;
        }
    }

}