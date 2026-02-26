package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record MindShimmerS2CPacket(boolean state) implements CustomPayload {
    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final CustomPayload.Id<MindShimmerS2CPacket> ID = new CustomPayload.Id<>(Identifier.of(Lulasmod.MOD_ID, "dash"));
    public static final PacketCodec<ByteBuf, MindShimmerS2CPacket> CODEC = PacketCodecs.codec(Codec.BOOL).xmap(MindShimmerS2CPacket::new, MindShimmerS2CPacket::state);
}
