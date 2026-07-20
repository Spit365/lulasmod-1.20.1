package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;

public record BleedProgressS2CPacket(int progress) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<BleedProgressS2CPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "bleed_progress"));
    public static final StreamCodec<ByteBuf, BleedProgressS2CPacket> CODEC = ByteBufCodecs.fromCodec(Codec.INT).map(integer -> new BleedProgressS2CPacket(integer == null ? 0 : integer), BleedProgressS2CPacket::progress);
}
