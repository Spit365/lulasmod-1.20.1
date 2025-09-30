package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SpellHotbar;

import java.util.ArrayList;
import java.util.List;

public class ModPackets {
	public static final Identifier TIME_FORWARD_ANIMATION = Identifier.of(Lulasmod.MOD_ID, "time_forward_animation");

	public record SpellHotbarListS2CPacket(List<ItemStack> list) implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<SpellHotbarListS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "spell_hotbar_list"));
		public static final PacketCodec<RegistryByteBuf, SpellHotbarListS2CPacket> CODEC =
			PacketCodecs.collection(ArrayList::new, ItemStack.PACKET_CODEC)
				.xmap(SpellHotbarListS2CPacket::new, spellHotbarListS2CPacket -> new ArrayList<>(spellHotbarListS2CPacket.list));
	}
	public record TimeForwardAnimationS2CPacket() implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<CycleSpellHotbarC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "time_forward_animation"));
		public static final PacketCodec<Object, CycleSpellHotbarC2SPacket> CODEC = PacketCodec.of((value, buf) -> {}, buf -> new CycleSpellHotbarC2SPacket());
	}

	public record CycleSpellHotbarC2SPacket() implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<CycleSpellHotbarC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "cycle_spell_hotbar"));
		public static final PacketCodec<Object, CycleSpellHotbarC2SPacket> CODEC = PacketCodec.of((value, buf) -> {}, buf -> new CycleSpellHotbarC2SPacket());
	}

	public static void init(){
		PayloadTypeRegistry.playS2C().register(SpellHotbarListS2CPacket.ID, SpellHotbarListS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(TimeForwardAnimationS2CPacket.ID, TimeForwardAnimationS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(CycleSpellHotbarC2SPacket.ID, CycleSpellHotbarC2SPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(ModPackets.CycleSpellHotbarC2SPacket.ID, (cycleSpellHotbarC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			for (Hand hand : Hand.values()) if (player.getStackInHand(hand).getItem() instanceof SpellHotbar item){
				item.onCycle(player);
				break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(ModPackets.SpellHotbarListS2CPacket.ID, (spellHotbarListS2CPacket, context) -> ModGui.SPELL_HOTBAR_LIST = spellHotbarListS2CPacket.list());
		ClientPlayNetworking.registerGlobalReceiver(ModPackets.TimeForwardAnimationS2CPacket.ID, (cycleSpellHotbarC2SPacket, context) -> ModClientTick.timeForwardAnimator.start());
	}
}
