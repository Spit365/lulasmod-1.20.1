package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModPackets;

public record CycleSpellHotbarC2SPacket() implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static final Type<CycleSpellHotbarC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "cycle_spell_hotbar"));
    public static final StreamCodec<Object, CycleSpellHotbarC2SPacket> CODEC = ModPackets.getEmptyPacketCodec(CycleSpellHotbarC2SPacket::new);
}
