package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;

public record MindShimmerS2CPacket(boolean state) implements CustomPacketPayload {
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final CustomPacketPayload.Type<MindShimmerS2CPacket> ID = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "dash"));
    public static final StreamCodec<ByteBuf, MindShimmerS2CPacket> CODEC = ByteBufCodecs.fromCodec(Codec.BOOL).map(MindShimmerS2CPacket::new, MindShimmerS2CPacket::state);
}
