package net.spit365.lulasmod.custom;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModItems;
import net.spit365.lulasmod.mod.ModSpells;
import net.spit365.lulasmod.util.ModUtil;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public final class Demon {
	public static void setDemon(Entity entity, boolean value) {
		entity.setAttached(ModData.DEMON, value);
	}

	public static boolean isDemon(@Nullable Entity entity) {
		return entity != null && entity.getAttachedOrElse(ModData.DEMON, false);
	}

	public static void give(@Nullable ServerPlayerEntity player) {
		if (player == null) return;
		if (!isDemon(player)) {
			player.sendMessage(Text.translatable("notify.lulasmod.command.contract_fail"), false);
			return;
		}

		List<Item> items = new LinkedList<>(ModSpells.SpellTabItems.stream()
			.map(ItemStack::getItem)
			.filter(ConjuringItem.class::isInstance)
			.toList());
		items.addFirst(ModItems.HELLISH_SEAL);

		boolean shouldDisplayMessage = true;
        int demonLevel = player.getAttachedOrElse(ModData.PRESENCE_LEVEL, 0) / 5;
		for (Item item : items) {
			if (demonLevel < 0) break;
			if (ModUtil.getInventoryStack(player, item) == null) player.giveItemStack(new ItemStack(item));
			else shouldDisplayMessage = false;
			demonLevel--;
		}
		if (shouldDisplayMessage)
			player.sendMessage(Text.translatable("notify.lulasmod.command.contract_success"), false);
	}
}
