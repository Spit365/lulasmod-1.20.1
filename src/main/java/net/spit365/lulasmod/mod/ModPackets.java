package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.DashSpell;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.packet.*;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.SpellHotbar;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ModPackets {

    public static void init(){
        PayloadTypeRegistry.playS2C().register(BleedProgressS2CPacket.ID, BleedProgressS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(DashSpellUsagesS2CPacket.ID, DashSpellUsagesS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(LightningLinkS2CPacket.ID, LightningLinkS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SetTimeForwardAnimationStateS2CPacket.ID, SetTimeForwardAnimationStateS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SpellHotbarListS2CPacket.ID, SpellHotbarListS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(SummonBleedS2CPacket.ID, SummonBleedS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(MindShimmerS2CPacket.ID, MindShimmerS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(CycleSpellHotbarC2SPacket.ID, CycleSpellHotbarC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DemonContractC2SPacket.ID, DemonContractC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DashC2SPacket.ID, DashC2SPacket.CODEC);

		ServerPlayNetworking.registerGlobalReceiver(CycleSpellHotbarC2SPacket.ID, (cycleSpellHotbarC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			for (Hand hand : Hand.values()) if (player.getStackInHand(hand).getItem() instanceof SpellHotbar item){
				item.onCycle(player, identifiers -> {
					List<Identifier> mutable = ModUtil.makeMutable(identifiers);
					Collections.rotate(mutable, player.isSneaking() ? 1 : -1);
					return mutable;
				});
				break;
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(DemonContractC2SPacket.ID, (demonContractC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			if (player == null) return;
            if (!Demon.isDemon(player)) {
                player.sendMessage(Text.translatable("notify.lulasmod.command.contract_fail"), false);
				return;
            }
			boolean shouldDisplayMessage = true;
			if (ModUtil.getInventoryStack(player, ModItems.HELLISH_SEAL) == null) player.giveItemStack(new ItemStack(ModItems.HELLISH_SEAL));
			else shouldDisplayMessage = false;
			for (ItemStack stack : ModSpells.SpellTabItems) {
				Item item = stack.getItem();
				if (!(item instanceof ConjuringItem)) continue;
				if (ModUtil.getInventoryStack(player, item) == null) player.giveItemStack(new ItemStack(item));
				else shouldDisplayMessage = false;
			}
			if (shouldDisplayMessage) player.sendMessage(Text.translatable("notify.lulasmod.command.contract_success"), false);
        });
		ServerPlayNetworking.registerGlobalReceiver(DashC2SPacket.ID, (dashC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			if (player == null || player.getAttached(ModData.APPLIED_SHIMMER_VARIANT) != Shimmer.Variant.PACE) return;
			DashSpell.dash((ServerWorld) player.getWorld(), player);
		});
	}

	public static @NotNull <T extends CustomPayload> PacketCodec<Object, T> getEmptyPacketCodec(Supplier<T> packet) {
		return PacketCodec.of((value, buf) -> {}, buf -> packet.get());
	}
}
