package net.spit365.lulasmod.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public record SummonBleedS2CPacket(Vec3 pos) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<SummonBleedS2CPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("client-tweaks", "summon_blood"));
    public static final StreamCodec<ByteBuf, SummonBleedS2CPacket> CODEC = ByteBufCodecs.fromCodec(Vec3.CODEC).map(SummonBleedS2CPacket::new, SummonBleedS2CPacket::pos);
}