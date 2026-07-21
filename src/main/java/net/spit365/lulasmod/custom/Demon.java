package net.spit365.lulasmod.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

	public static void give(@Nullable ServerPlayer player) {
		if (player == null) return;
		if (!isDemon(player)) {
			player.sendOverlayMessage(Component.translatable("notify.lulasmod.command.contract_fail"));
			return;
		}

		List<Item> items = new LinkedList<>(ModSpells.SpellTabItems.stream()
			.map(itemStackSupplier -> itemStackSupplier.get().getItem())
			.filter(ConjuringItem.class::isInstance)
			.toList());
		items.addFirst(ModItems.HELLISH_SEAL);

		boolean shouldDisplayMessage = true;
        int demonLevel = player.getAttachedOrElse(ModData.PRESENCE_LEVEL, 0) / 5;
		for (Item item : items) {
			if (demonLevel < 0) break;
			if (ModUtil.getInventoryStack(player, item) == null) player.addItem(new ItemStack(item));
			else shouldDisplayMessage = false;
			demonLevel--;
		}
		if (shouldDisplayMessage)
			player.sendOverlayMessage(Component.translatable("notify.lulasmod.command.contract_success"));
	}
}
