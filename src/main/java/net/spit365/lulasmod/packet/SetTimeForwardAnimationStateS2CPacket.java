package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record SetTimeForwardAnimationStateS2CPacket(boolean state) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<SetTimeForwardAnimationStateS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "time_forward_animation"));
    public static final PacketCodec<ByteBuf, SetTimeForwardAnimationStateS2CPacket> CODEC = PacketCodecs.codec(Codec.BOOL).xmap(SetTimeForwardAnimationStateS2CPacket::new, SetTimeForwardAnimationStateS2CPacket::state);
}
