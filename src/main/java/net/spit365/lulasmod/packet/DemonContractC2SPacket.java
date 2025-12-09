package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record DemonContractC2SPacket() implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<DemonContractC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "demon_contract"));
    public static final PacketCodec<Object, DemonContractC2SPacket> CODEC = ModPackets.getEmptyPacketCodec(DemonContractC2SPacket::new);
}
