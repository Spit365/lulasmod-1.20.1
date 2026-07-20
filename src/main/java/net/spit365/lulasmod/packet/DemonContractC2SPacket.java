package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record DemonContractC2SPacket() implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<DemonContractC2SPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "demon_contract"));
    public static final StreamCodec<Object, DemonContractC2SPacket> CODEC = ModPackets.getEmptyPacketCodec(DemonContractC2SPacket::new);
}
