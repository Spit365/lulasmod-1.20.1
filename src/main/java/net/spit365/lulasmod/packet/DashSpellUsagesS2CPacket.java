package net.spit365.lulasmod.packet;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record DashSpellUsagesS2CPacket(int usages) implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<DashSpellUsagesS2CPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "dash_spell_usages"));
    public static final StreamCodec<ByteBuf, DashSpellUsagesS2CPacket> CODEC = ByteBufCodecs.fromCodec(Codec.INT).map(integer -> new DashSpellUsagesS2CPacket(integer == null ? -1 : integer), DashSpellUsagesS2CPacket::usages);
}
