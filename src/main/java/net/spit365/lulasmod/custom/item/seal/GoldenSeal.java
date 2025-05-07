package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;

public class GoldenSeal extends SealItem{
    @Override protected Boolean canUse(PlayerEntity player) {return true;}
    @Override protected Float efficiencyMultiplier() {return 1f;}
    @Override protected Integer cooldownMultiplier() {return 2;}
}