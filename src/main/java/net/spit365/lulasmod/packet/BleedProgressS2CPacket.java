package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record BleedProgressS2CPacket(int progress) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<BleedProgressS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "bleed_progress"));
    public static final PacketCodec<ByteBuf, BleedProgressS2CPacket> CODEC = PacketCodecs.codec(Codec.INT).xmap(integer -> new BleedProgressS2CPacket(integer == null ? 0 : integer), BleedProgressS2CPacket::progress);
}
