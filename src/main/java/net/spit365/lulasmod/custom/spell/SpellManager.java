package net.spit365.lulasmod.custom.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.spit365.lulasmod.Lulasmod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SpellManager {
    private static final Map<UUID, LinkedList<ItemStack>> playerSpells = new HashMap<>();
    private static final Map<UUID, Integer> playerSpellSlots = new HashMap<>();
    private static final Map<UUID, Integer> playerDashSpellUsages = new HashMap<>();

    public static LinkedList<ItemStack> getSpells(@NotNull PlayerEntity player) {
        return playerSpells.computeIfAbsent(player.getUuid(), k -> new LinkedList<>());
    }

    public static void addSpell(PlayerEntity player, ItemStack spell) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (spells.size() >= getPlayerSpellSlots(player)) spells.clear();
        spells.add(spell.copy());
    }

    public static void cycleSpells(PlayerEntity player) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (!spells.isEmpty()) {
            ItemStack first = spells.pollFirst();
            spells.addLast(first);
        }
    }


    public static Integer getPlayerSpellSlots(@NotNull PlayerEntity player) {
        return (playerSpellSlots.get(player.getUuid()) != null? playerSpellSlots.get(player.getUuid()) : 3);
    }

    public static void setPlayerSpellSlots(@NotNull PlayerEntity player, Integer slots) {
        playerSpellSlots.put(player.getUuid(), slots);
    }
    public static void addPlayerSpellSlots(@NotNull PlayerEntity player, Integer slots) {
        setPlayerSpellSlots(player, getPlayerSpellSlots(player) + slots);
    }


    public static Integer getPlayerDashSpellUsages(@NotNull PlayerEntity player){
        return (playerDashSpellUsages.get(player.getUuid()) == null? -1 : playerDashSpellUsages.get(player.getUuid()));
    }
    
    public static void setPlayerDashSpellUsages(@NotNull PlayerEntity player, Integer usages){
        playerDashSpellUsages.put(player.getUuid(), usages);
    }
    public static void decreasePlayerDashSpellUsages(@NotNull PlayerEntity player){
        setPlayerDashSpellUsages(player, getPlayerDashSpellUsages(player) -1);
    }
    public static void managePlayerSpellUsages(PlayerEntity player, Item item, Integer usages, Integer delayMin, Integer delayMax){
        SpellManager.decreasePlayerDashSpellUsages(player);
        if (SpellManager.getPlayerDashSpellUsages(player) < 0){
            SpellManager.setPlayerDashSpellUsages(player, usages);
        }
        player.getItemCooldownManager().set(item, (SpellManager.getPlayerSpellSlots(player) == 0? delayMax : delayMin));
        Lulasmod.LOGGER.warn("Managed Spell Usages of Player:{} to Usages:{} Cooldown:{}", player.getName(), SpellManager.getPlayerDashSpellUsages(player).toString(), SpellManager.getPlayerSpellSlots(player) == 0 ? delayMax : delayMin);
    }
}