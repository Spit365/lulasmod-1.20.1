package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;
import net.spit365.lulasmod.mod.ModDamageSources;

public class BloodsuckingSeal extends SealItem{
    public BloodsuckingSeal(Settings settings) {
        super(settings);
    }

    @Override protected Boolean canUse(PlayerEntity player) {player.damage(ModDamageSources.DIABLOS_FLAME(player), 3); return true;}
    @Override protected Float damageMultiplier() {return 2f;}
    @Override protected Integer cooldownMultiplier() {return 1;}
}
