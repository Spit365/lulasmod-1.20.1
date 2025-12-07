package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record TimeForwardAnimationS2CPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<TimeForwardAnimationS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "time_forward_animation"));
    public static final PacketCodec<Object, TimeForwardAnimationS2CPacket> CODEC = ModPackets.getEmptyPacketCodec(TimeForwardAnimationS2CPacket::new);
}
