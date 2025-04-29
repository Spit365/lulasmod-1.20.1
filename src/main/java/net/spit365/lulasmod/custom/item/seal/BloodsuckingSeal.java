package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.spit365.lulasmod.mod.ModStatusEffects;

public class BloodsuckingSeal extends SealItem{
    public BloodsuckingSeal(Settings settings) {
        super(settings);
    }

    public static void applyBleed(LivingEntity entity, Integer duration){
        StatusEffectInstance effectInstance = entity.getStatusEffect(ModStatusEffects.BLEEDING);
        if (effectInstance != null){
            entity.setStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, effectInstance.getDuration() + duration), entity);
        } else entity.addStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, duration));
    }

    @Override protected Boolean canUse(PlayerEntity player) {
        applyBleed(player, 100);
        return true;
    }
    @Override protected Float efficiencyMultiplier() {return 2f;}
    @Override protected Integer cooldownMultiplier() {return 1;}
}
