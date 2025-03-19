package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class SpellManager {
    private static final Map<UUID, LinkedList<ItemStack>> playerSpells = new HashMap<>();

    public static LinkedList<ItemStack> getSpells(PlayerEntity player) {
        return playerSpells.computeIfAbsent(player.getUuid(), k -> new LinkedList<>());
    }

    public static void addSpell(PlayerEntity player, ItemStack spell) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (spells.size() >= 3) spells.clear();
        spells.add(spell.copy());
    }

    public static void cycleSpells(PlayerEntity player) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (!spells.isEmpty()) {
            ItemStack first = spells.pollFirst();
            spells.addLast(first);
        }
    }
}