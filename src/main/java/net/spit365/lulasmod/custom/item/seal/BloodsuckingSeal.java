package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.LivingEntity;
import net.spit365.lulasmod.mod.ModMethods;

public class BloodsuckingSeal extends SealItem{
    @Override public Boolean canUse(LivingEntity entity) {
        ModMethods.applyBleed(entity, 100);
        return true;
    }
    @Override public Float efficiencyMultiplier() {return 2f;}
    @Override public Integer cooldownMultiplier() {return 1;}
}