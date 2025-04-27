package net.spit365.lulasmod.custom.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModStatusEffects;

import java.util.Objects;

public class BleedingStatusEffect extends StatusEffect {
  public BleedingStatusEffect() {super(StatusEffectCategory.HARMFUL,0xac2726); }

  @Override public boolean canApplyUpdateEffect(int duration, int amplifier) {return duration >= 1200;}
  @Override public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.setStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, Objects.requireNonNull(entity.getStatusEffect(ModStatusEffects.BLEEDING)).getDuration() -1200), entity);
    entity.damage(ModDamageSources.BLOODSUCKING(entity), entity.getMaxHealth() * 0.3f + 10f);
  }
}