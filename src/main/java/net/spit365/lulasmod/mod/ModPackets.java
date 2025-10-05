package net.spit365.lulasmod.mod;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.manager.MultiVec3d;
import net.spit365.lulasmod.manager.TimeForwardAnimator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class ModPackets {
    public static final Set<MultiVec3d> linkedLightnings = new HashSet<>();

    public record SpellHotbarListS2CPacket(List<ItemStack> list) implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<SpellHotbarListS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "spell_hotbar_list"));
		public static final PacketCodec<RegistryByteBuf, SpellHotbarListS2CPacket> CODEC =
			PacketCodecs.collection(ArrayList::new, ItemStack.PACKET_CODEC)
				.xmap(SpellHotbarListS2CPacket::new, spellHotbarListS2CPacket -> new ArrayList<>(spellHotbarListS2CPacket.list));
	}
	public record TimeForwardAnimationS2CPacket() implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<TimeForwardAnimationS2CPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "time_forward_animation"));
		public static final PacketCodec<Object, TimeForwardAnimationS2CPacket> CODEC = getEmptyCodec(TimeForwardAnimationS2CPacket::new);
	}
	public record LightningLinkS2CPacket(Set<MultiVec3d> linkedLightning) implements CustomPayload {
        @Override public Id<? extends CustomPayload> getId() {return ID;}
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

	public record CycleSpellHotbarC2SPacket() implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<CycleSpellHotbarC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "cycle_spell_hotbar"));
		public static final PacketCodec<Object, CycleSpellHotbarC2SPacket> CODEC = getEmptyCodec(CycleSpellHotbarC2SPacket::new);
	}
	public record TailedContractC2SPacket() implements CustomPayload {
		@Override public Id<? extends CustomPayload> getId() {return ID;}
		public static final Id<TailedContractC2SPacket> ID = new Id<>(Identifier.of(Lulasmod.MOD_ID, "tailed_contract"));
		public static final PacketCodec<Object, TailedContractC2SPacket> CODEC = getEmptyCodec(TailedContractC2SPacket::new);
	}

	public static void init(){
		PayloadTypeRegistry.playS2C().register(SpellHotbarListS2CPacket.ID, SpellHotbarListS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(TimeForwardAnimationS2CPacket.ID, TimeForwardAnimationS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(LightningLinkS2CPacket.ID, LightningLinkS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(CycleSpellHotbarC2SPacket.ID, CycleSpellHotbarC2SPacket.CODEC);


		ServerPlayNetworking.registerGlobalReceiver(CycleSpellHotbarC2SPacket.ID, (cycleSpellHotbarC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			for (Hand hand : Hand.values()) if (player.getStackInHand(hand).getItem() instanceof SpellHotbar item){
				item.onCycle(player);
				break;
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SpellHotbarListS2CPacket.ID, (spellHotbarListS2CPacket, context) -> ModGui.SPELL_HOTBAR_LIST = spellHotbarListS2CPacket.list());
		ClientPlayNetworking.registerGlobalReceiver(TimeForwardAnimationS2CPacket.ID, (cycleSpellHotbarC2SPacket, context) -> TimeForwardAnimator.start(context.client().world));
        ClientPlayNetworking.registerGlobalReceiver(LightningLinkS2CPacket.ID, (payload, context) -> {
            linkedLightnings.clear();
            linkedLightnings.addAll(payload.linkedLightning());
        });
	}

	private static @NotNull <T extends CustomPayload> PacketCodec<Object, T> getEmptyCodec(Supplier<T> packet) {
		return PacketCodec.of((value, buf) -> {
		}, buf -> packet.get());
	}
}
