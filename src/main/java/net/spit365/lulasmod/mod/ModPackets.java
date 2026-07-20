package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.spit365.lulasmod.custom.DashSpell;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.packet.*;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.SpellHotbar;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ModPackets {

    public static void init() {
		//S2C Packets
        PayloadTypeRegistry.playS2C().register(BleedProgressS2CPacket.ID, BleedProgressS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(DashSpellUsagesS2CPacket.ID, DashSpellUsagesS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(LightningLinkS2CPacket.ID, LightningLinkS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SetTimeForwardAnimationStateS2CPacket.ID, SetTimeForwardAnimationStateS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SpellHotbarListS2CPacket.ID, SpellHotbarListS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SummonBleedS2CPacket.ID, SummonBleedS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(MindShimmerS2CPacket.ID, MindShimmerS2CPacket.CODEC);

		//C2S Packets
		PayloadTypeRegistry.playC2S().register(CycleSpellHotbarC2SPacket.ID, CycleSpellHotbarC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DemonContractC2SPacket.ID, DemonContractC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DashC2SPacket.ID, DashC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(BreakWithMindC2SPacket.ID, BreakWithMindC2SPacket.CODEC);

		//Serverside Receivers
		ServerPlayNetworking.registerGlobalReceiver(CycleSpellHotbarC2SPacket.ID, (cycleSpellHotbarC2SPacket, context) -> {
			ServerPlayer player = context.player();
			for (InteractionHand hand : InteractionHand.values()) if (player.getItemInHand(hand).getItem() instanceof SpellHotbar item) {
				item.onCycle(player, identifiers -> {
					List<ResourceLocation> mutable = ModUtil.makeMutable(identifiers);
					Collections.rotate(mutable, player.isShiftKeyDown() ? 1 : -1);
					return mutable;
				});
				break;
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(DemonContractC2SPacket.ID, (demonContractC2SPacket, context) ->
			Demon.give(context.player()));

		ServerPlayNetworking.registerGlobalReceiver(DashC2SPacket.ID, (dashC2SPacket, context) -> {
			ServerPlayer player = context.player();
			if (player == null || player.getAttached(ModData.APPLIED_SHIMMER_VARIANT) != Shimmer.Variant.PACE) return;
			DashSpell.dash(player.level(), player);
		});

		ServerPlayNetworking.registerGlobalReceiver(BreakWithMindC2SPacket.ID, (breakWithMindC2SPacket, context) -> {
			ServerPlayer player = context.player();
			ModPresences.breakWithMind(player, player.level());
		});
	}

	public static @NotNull <T extends CustomPacketPayload> StreamCodec<Object, T> getEmptyPacketCodec(Supplier<T> packet) {
		return StreamCodec.ofMember((value, buf) -> {}, buf -> packet.get());
	}
}
