package net.spit365.lulasmod.packet;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

import java.util.ArrayList;
import java.util.List;

public record SpellHotbarListS2CPacket(List<ItemStack> list) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<SpellHotbarListS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "spell_hotbar_list"));
    public static final PacketCodec<RegistryByteBuf, SpellHotbarListS2CPacket> CODEC =
        PacketCodecs.collection(ArrayList::new, ItemStack.PACKET_CODEC)
            .xmap(SpellHotbarListS2CPacket::new, spellHotbarListS2CPacket -> new ArrayList<>(spellHotbarListS2CPacket.list));
}
