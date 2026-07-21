package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record BreakWithMindC2SPacket() implements CustomPacketPayload {
	public static final Type<BreakWithMindC2SPacket> ID = new Type<>(Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "break_with_mind"));
	public static final StreamCodec<Object, BreakWithMindC2SPacket> CODEC = StreamCodec.ofMember((_, _) -> {}, _ -> new BreakWithMindC2SPacket());
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}
}

