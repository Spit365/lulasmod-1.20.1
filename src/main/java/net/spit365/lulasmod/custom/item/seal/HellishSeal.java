package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.LivingEntity;

public class HellishSeal extends SealItem{
    @Override
    public Boolean canUse(LivingEntity entity) {return entity.getCommandTags().contains("tailed");}
    @Override
    public Float efficiencyMultiplier(){return 2f;}
    @Override
    public Integer cooldownMultiplier() {return 1;}
}