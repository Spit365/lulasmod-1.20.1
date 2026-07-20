package net.spit365.lulasmod.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.util.MultiVec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record LightningLinkS2CPacket(Set<MultiVec3d> linkedLightning) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<LightningLinkS2CPacket> ID = new Type<>(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "lightning_links"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LightningLinkS2CPacket> CODEC =
        StreamCodec.ofMember(
            (value, buf) -> new FriendlyByteBuf(buf).writeJsonWithCodec(
                MultiVec3d.CODEC.listOf().xmap(HashSet::new, ArrayList::new),
                value.linkedLightning()
            ),
            buf -> new LightningLinkS2CPacket(
                new FriendlyByteBuf(buf).readLenientJsonWithCodec(
                    MultiVec3d.CODEC.listOf().xmap(HashSet::new, ArrayList::new)
                )
            )
        );

}
