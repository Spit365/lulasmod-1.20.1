package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.LivingEntity;

public class GoldenSeal extends SealItem{
    @Override
    public Boolean canUse(LivingEntity entity) {return true;}
    @Override
    public Float efficiencyMultiplier() {return 1f;}
    @Override
    public Integer cooldownMultiplier() {return 2;}
}