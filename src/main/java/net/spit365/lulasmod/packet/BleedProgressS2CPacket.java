package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record BleedProgressS2CPacket(int progress) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<BleedProgressS2CPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "bleed_progress"));
    public static final StreamCodec<ByteBuf, BleedProgressS2CPacket> CODEC = ByteBufCodecs.fromCodec(Codec.INT).map(BleedProgressS2CPacket::new, BleedProgressS2CPacket::progress);
}
