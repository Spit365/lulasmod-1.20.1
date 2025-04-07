package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;

public class HellishSeal extends SealItem{
    public HellishSeal(Settings settings) {
        super(settings);
    }

    @Override protected Boolean canUse(PlayerEntity player) {return player.getCommandTags().contains("tailed");}
    @Override protected Float damageMultiplier(){return 2f;}
    @Override protected Integer cooldownMultiplier() {return 1;}
}
