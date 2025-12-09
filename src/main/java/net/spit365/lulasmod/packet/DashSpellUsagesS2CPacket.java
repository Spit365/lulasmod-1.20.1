package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record DashSpellUsagesS2CPacket(int usages) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<DashSpellUsagesS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "dash_spell_usages"));
    public static final PacketCodec<ByteBuf, DashSpellUsagesS2CPacket> CODEC = PacketCodecs.codec(Codec.INT).xmap(integer -> new DashSpellUsagesS2CPacket(integer == null ? -1 : integer), DashSpellUsagesS2CPacket::usages);
}
