package net.spit365.lulasmod.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public interface SpellHotbar {
     List<Identifier> getHotbarList(PlayerEntity player);
     void onCycle(PlayerEntity player);
}