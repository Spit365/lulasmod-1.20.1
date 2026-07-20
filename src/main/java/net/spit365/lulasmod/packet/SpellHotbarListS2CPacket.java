package net.spit365.lulasmod.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.spit365.lulasmod.Lulasmod;

import java.util.ArrayList;
import java.util.List;

public record SpellHotbarListS2CPacket(List<ItemStack> list) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<SpellHotbarListS2CPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "spell_hotbar_list"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellHotbarListS2CPacket> CODEC =
        ByteBufCodecs.collection(ArrayList::new, ItemStack.STREAM_CODEC)
            .map(SpellHotbarListS2CPacket::new, spellHotbarListS2CPacket -> new ArrayList<>(spellHotbarListS2CPacket.list));
}
