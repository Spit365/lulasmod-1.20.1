package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record TailedContractC2SPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<TailedContractC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "tailed_contract"));
    public static final PacketCodec<Object, TailedContractC2SPacket> CODEC = ModPackets.getEmptyPacketCodec(TailedContractC2SPacket::new);
}
