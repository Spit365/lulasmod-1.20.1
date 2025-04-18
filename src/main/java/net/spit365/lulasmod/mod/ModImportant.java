package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.IncantationItem;
import net.spit365.lulasmod.tag.TagManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ModImportant {
    public static final String PocketDimensionKey = "ResourceKey[minecraft:dimension / " + Lulasmod.MOD_ID + ":pocket_dimension]";


    public static void summonSmoke(@NotNull Vec3d position, World world){
        ((ServerWorld) world).spawnParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                position.x, position.y + 1.0d, position.z,
                269, 1.2d, 1.2d, 1.2d, 0.0d
        );
    }

    public static ItemStack getItemStackFromSpell(String spell){
        for (Identifier id : ModItems.IncantationItems)
            if (((IncantationItem) Registries.ITEM.get(id)).getSpellName().equals(spell)) return new ItemStack(Registries.ITEM.get(id));
        return null;
    }

    public static void updateClientSpellList(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
            PacketByteBuf buf = PacketByteBufs.create();
            LinkedList<String> list = new LinkedList<>(TagManager.readList(player, ModTagCategories.SPELLS));
            Map<Integer, ItemStack> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) map.put(i, getItemStackFromSpell(list.get(i)));
            buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
            ServerPlayNetworking.send(player, ModPackets.PLAYER_SPELL_LIST, buf);
        }
    }
}
