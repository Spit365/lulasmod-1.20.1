package net.spit365.lulasmod.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public interface SpellHotbar {
     LinkedList<Identifier> displayList(PlayerEntity player);
     void cycleList(PlayerEntity player);
}