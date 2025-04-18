package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;

public class CreativeSeal extends SealItem{
    public CreativeSeal(Settings settings) {
        super(settings);
    }

    @Override protected Boolean canUse(PlayerEntity player) {return player.isCreative();}
    @Override protected Float efficiencyMultiplier() {return Float.MAX_VALUE;}
    @Override protected Integer cooldownMultiplier() {return Integer.MAX_VALUE;}
}
