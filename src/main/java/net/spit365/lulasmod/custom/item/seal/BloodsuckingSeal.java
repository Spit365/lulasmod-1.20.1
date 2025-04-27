package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModStatusEffects;

public class BloodsuckingSeal extends SealItem{
    public BloodsuckingSeal(Settings settings) {
        super(settings);
    }

    @Override protected Boolean canUse(PlayerEntity player) {
        StatusEffectInstance effectInstance = player.getStatusEffect(ModStatusEffects.BLEEDING);
        if (effectInstance != null){
            player.setStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, effectInstance.getDuration() + 100), player);
        } else player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, 100));
        return true;
    }
    @Override protected Float efficiencyMultiplier() {return 2f;}
    @Override protected Integer cooldownMultiplier() {return 1;}
}
