package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record DashC2SPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<DashC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "dash"));
    public static final PacketCodec<Object, DashC2SPacket> CODEC = ModPackets.getEmptyPacketCodec(DashC2SPacket::new);
}
