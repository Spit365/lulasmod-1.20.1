package net.spit365.lulasmod.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;

public interface SpellHotbar {
     List<Identifier> displayList(PlayerEntity player);
     void cycleList(PlayerEntity player);
}