package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record DashC2SPacket() implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<DashC2SPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "dash"));
    public static final StreamCodec<Object, DashC2SPacket> CODEC = ModPackets.getEmptyPacketCodec(DashC2SPacket::new);
}
