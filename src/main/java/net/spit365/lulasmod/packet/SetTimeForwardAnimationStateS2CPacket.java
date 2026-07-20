package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;

public record SetTimeForwardAnimationStateS2CPacket(boolean state) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<SetTimeForwardAnimationStateS2CPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "time_forward_animation"));
    public static final StreamCodec<ByteBuf, SetTimeForwardAnimationStateS2CPacket> CODEC = ByteBufCodecs.fromCodec(Codec.BOOL).map(SetTimeForwardAnimationStateS2CPacket::new, SetTimeForwardAnimationStateS2CPacket::state);
}
