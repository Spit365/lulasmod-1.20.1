package net.spit365.lulasmod.custom.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.spit365.lulasmod.mod.Mod;

import java.util.Objects;

public class BleedingStatusEffect extends StatusEffect {
  public BleedingStatusEffect() {super(StatusEffectCategory.HARMFUL,0xac2726); }

  @Override public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}
  @Override public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    int duration = Objects.requireNonNull(entity.getStatusEffect(Mod.StatusEffects.BLEEDING)).getDuration();
    int min = Math.min((int) (Math.min(entity.getHealth(), entity.getMaxHealth()) * 60) -1, 1200);
    if (duration > min) {
      entity.setStatusEffect(new StatusEffectInstance(Mod.StatusEffects.BLEEDING, duration - min), entity);
      entity.damage(Mod.DamageSources.BLOODSUCKING(entity), entity.getMaxHealth() * 0.15f + 10f);
    }
  }
}