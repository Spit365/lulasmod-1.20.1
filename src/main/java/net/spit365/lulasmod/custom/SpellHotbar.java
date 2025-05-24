package net.spit365.lulasmod.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;

public interface SpellHotbar {
     LinkedList<Identifier> display(PlayerEntity player);
     void cycle(PlayerEntity player);
}