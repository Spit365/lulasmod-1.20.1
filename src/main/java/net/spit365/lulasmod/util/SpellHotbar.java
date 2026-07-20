package net.spit365.lulasmod.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spit365.lulasmod.packet.SpellHotbarListS2CPacket;

import java.util.List;
import java.util.function.Function;

public interface SpellHotbar {
    List<ResourceLocation> getHotbarList(Player player);
    void onCycle(Player player, Function<List<ResourceLocation>, List<ResourceLocation>> cycleFunction);

    static void tick(ServerPlayer player) {
        for (InteractionHand hand : InteractionHand.values()) {
            if (!(player.getItemInHand(hand).getItem() instanceof SpellHotbar item)) continue;
            List<ResourceLocation> hotbarList = item.getHotbarList(player);
            if (hotbarList != null) ServerPlayNetworking.send(player, new SpellHotbarListS2CPacket(hotbarList.stream().map(id -> new ItemStack(BuiltInRegistries.ITEM.getValue(id))).toList()));
            break;
        }
    }

}