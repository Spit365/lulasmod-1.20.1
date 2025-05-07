package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;

public class HellishSeal extends SealItem{
    @Override protected Boolean canUse(PlayerEntity player) {return player.getCommandTags().contains("tailed");}
    @Override protected Float efficiencyMultiplier(){return 2f;}
    @Override protected Integer cooldownMultiplier() {return 1;}
}