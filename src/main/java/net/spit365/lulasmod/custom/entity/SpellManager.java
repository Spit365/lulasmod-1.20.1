package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.*;

public class SpellManager {
    private static final Map<UUID, LinkedList<ItemStack>> playerSpells = new HashMap<>();
    private static final Map<UUID, Integer> playerSpellSlots = new HashMap<>();

    public static LinkedList<ItemStack> getSpells(PlayerEntity player) {
        return playerSpells.computeIfAbsent(player.getUuid(), k -> new LinkedList<>());
    }

    public static void addSpell(PlayerEntity player, ItemStack spell) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (spells.size() >= 5) spells.clear();
        spells.add(spell.copy());
    }

    public static void cycleSpells(PlayerEntity player) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (!spells.isEmpty()) {
            ItemStack first = spells.pollFirst();
            spells.addLast(first);
        }
    }

    public static Integer getPlayerSpellSlots(PlayerEntity player) {
        return (playerSpellSlots.get(player.getUuid()) != null? playerSpellSlots.get(player.getUuid()) : 3);
    }

    public static void setPlayerSpellSlots(PlayerEntity player, Integer slots) {
        SpellManager.playerSpellSlots.put(player.getUuid(), slots);
    }
    public static void addPlayerSpellSlots(PlayerEntity player, Integer slots) {
        SpellManager.playerSpellSlots.put(player.getUuid(), SpellManager.getPlayerSpellSlots(player) + slots);
    }
}