package net.spit365.lulasmod.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record SummonBleedS2CPacket(Vec3d pos) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<SummonBleedS2CPacket> ID = new Id<>(Identifier.of("client-tweaks", "summon_blood"));
    public static final PacketCodec<ByteBuf, SummonBleedS2CPacket> CODEC = PacketCodecs.codec(Vec3d.CODEC).xmap(SummonBleedS2CPacket::new, SummonBleedS2CPacket::pos);
}