package net.spit365.lulasmod.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.util.MultiVec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record LightningLinkS2CPacket(Set<MultiVec3d> linkedLightning) implements CustomPayload {
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static final Id<LightningLinkS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "lightning_links"));
    public static final PacketCodec<RegistryByteBuf, LightningLinkS2CPacket> CODEC =
        PacketCodec.of(
            (value, buf) -> new PacketByteBuf(buf).encodeAsJson(
                MultiVec3d.CODEC.listOf().xmap(HashSet::new, ArrayList::new),
                value.linkedLightning()
            ),
            buf -> new LightningLinkS2CPacket(
                new PacketByteBuf(buf).decodeAsJson(
                    MultiVec3d.CODEC.listOf().xmap(HashSet::new, ArrayList::new)
                )
            )
        );

}
