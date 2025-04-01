package net.spit365.lulasmod.custom.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import static net.spit365.lulasmod.mod.ModItems.IncantationItems;

public class SpellManager {
    private static final Map<UUID, LinkedList<ItemStack>> playerSpells = new HashMap<>();
    private static final Map<UUID, Integer> playerDashSpellUsages = new HashMap<>();

    public static LinkedList<ItemStack> getSpells(@NotNull PlayerEntity player) {
        return playerSpells.computeIfAbsent(player.getUuid(), k -> new LinkedList<>());
    }

    public static void addSpell(PlayerEntity player, ItemStack spell) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (spells.size() >= (player.getCommandTags().contains("tailed")? IncantationItems.size() : 3)) spells.clear();
        spells.add(spell.copy());
    }

    public static void cycleSpells(PlayerEntity player) {
        LinkedList<ItemStack> spells = getSpells(player);
        if (!spells.isEmpty()) {
            ItemStack first = spells.pollFirst();
            spells.addLast(first);
        }
    }

    private static Integer getDashSpellUsages(@NotNull PlayerEntity player){
        return (playerDashSpellUsages.get(player.getUuid()) == null? -1 : playerDashSpellUsages.get(player.getUuid()));
    }
    
    private static void setDashSpellUsages(@NotNull PlayerEntity player, Integer usages){
        playerDashSpellUsages.put(player.getUuid(), usages);
    }

    public static void manageDashSpellUsages(PlayerEntity player, Item item, Integer usages, Integer delayMin, Integer delayMax){
        setDashSpellUsages(player, getDashSpellUsages(player) -1);
        if (SpellManager.getDashSpellUsages(player) < 0){
            SpellManager.setDashSpellUsages(player, usages -1);
        }
        player.getItemCooldownManager().set(item, (SpellManager.getDashSpellUsages(player).equals(0) ? delayMax : delayMin));
    }
}