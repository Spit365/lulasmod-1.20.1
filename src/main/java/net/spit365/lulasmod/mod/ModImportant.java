package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.tag.TagManager;

import java.util.*;

public class ModImportant {

    public static void updateClientSpellList(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            PacketByteBuf buf = PacketByteBufs.create();
            LinkedList<Identifier> list = TagManager.readList(player, ModTagCategories.SPELLS);

            Map<Integer, ItemStack> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) map.put(i, new ItemStack(Registries.ITEM.get(list.get(i))));
            buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
            ServerPlayNetworking.send(player, ModPackets.PLAYER_SPELL_LIST, buf);
        }
    }
}
