package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.custom.LinkedLightning;
import net.spit365.lulasmod.custom.TimeForward;
import net.spit365.lulasmod.packet.*;
import net.spit365.lulasmod.renderer.DashSpellUsagesRenderer;
import net.spit365.lulasmod.util.SpellHotbar;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ModPackets {

    public static void init(){
        PayloadTypeRegistry.playS2C().register(SummonBleedS2CPacket.ID, SummonBleedS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(SpellHotbarListS2CPacket.ID, SpellHotbarListS2CPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(TimeForwardAnimationS2CPacket.ID, TimeForwardAnimationS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(LightningLinkS2CPacket.ID, LightningLinkS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(DashSpellUsagesS2CPacket.ID, DashSpellUsagesS2CPacket.CODEC);

		PayloadTypeRegistry.playC2S().register(CycleSpellHotbarC2SPacket.ID, CycleSpellHotbarC2SPacket.CODEC);
		PayloadTypeRegistry.playC2S().register(DemonContractC2SPacket.ID, DemonContractC2SPacket.CODEC);


		ServerPlayNetworking.registerGlobalReceiver(CycleSpellHotbarC2SPacket.ID, (cycleSpellHotbarC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			for (Hand hand : Hand.values()) if (player.getStackInHand(hand).getItem() instanceof SpellHotbar item){
				item.onCycle(player);
				break;
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(DemonContractC2SPacket.ID, (demonContractC2SPacket, context) -> {
			ServerPlayerEntity player = context.player();
			if (player == null) return;
			if (Demon.isDemon(player)){
				boolean shouldDisplayMessage = true;
				if (ModMethods.getInventoryStack(player, ModItems.HELLISH_SEAL) == null) player.giveItemStack(new ItemStack(ModItems.HELLISH_SEAL));
				else shouldDisplayMessage = false;
				for (Identifier id : ModSpells.SpellTabItems) {
					Item item = Registries.ITEM.get(id);
					if (!(item instanceof ConjuringItem)) continue;
					if (ModMethods.getInventoryStack(player, item) == null) player.giveItemStack(new ItemStack(item));
					else if (shouldDisplayMessage) shouldDisplayMessage = false;
				}
				if (shouldDisplayMessage) player.sendMessage(Text.translatable("notify.lulasmod.command.contract_success"), false);
			} else player.sendMessage(Text.translatable("notify.lulasmod.command.contract_fail"), false);
		});

		ClientPlayNetworking.registerGlobalReceiver(DashSpellUsagesS2CPacket.ID, (dashSpellUsagesS2CPacket, context) -> DashSpellUsagesRenderer.usages = dashSpellUsagesS2CPacket.usages());
		ClientPlayNetworking.registerGlobalReceiver(SummonBleedS2CPacket.ID, (summonBleedS2CPacket, context) -> Bleed.summonParticles(summonBleedS2CPacket.getPos(), context.client().world));
		ClientPlayNetworking.registerGlobalReceiver(SpellHotbarListS2CPacket.ID, (spellHotbarListS2CPacket, context) -> ModGui.SPELL_HOTBAR_LIST = spellHotbarListS2CPacket.list());
		ClientPlayNetworking.registerGlobalReceiver(TimeForwardAnimationS2CPacket.ID, (cycleSpellHotbarC2SPacket, context) -> TimeForward.Animator.start(context.client().world));
        ClientPlayNetworking.registerGlobalReceiver(LightningLinkS2CPacket.ID, (payload, context) -> {
            LinkedLightning.Render.linkedLightnings.clear();
            LinkedLightning.Render.linkedLightnings.addAll(payload.linkedLightning());
        });
	}

	public static @NotNull <T extends CustomPayload> PacketCodec<Object, T> getEmptyPacketCodec(Supplier<T> packet) {
		return PacketCodec.of((value, buf) -> {}, buf -> packet.get());
	}
}
