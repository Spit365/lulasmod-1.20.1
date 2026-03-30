package net.spit365.lulasmod.packet;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public record BreakWithMindC2SPacket() implements CustomPayload {
	public static final Id<BreakWithMindC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "break_with_mind"));
	public static final PacketCodec<Object, BreakWithMindC2SPacket> CODEC = PacketCodec.of((value, buf) -> {}, buf -> new BreakWithMindC2SPacket());
	@Override
	public Id<? extends CustomPayload> getId() {
		return ID;
	}
}

