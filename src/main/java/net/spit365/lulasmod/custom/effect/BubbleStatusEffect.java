package net.spit365.lulasmod.custom.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class BubbleStatusEffect extends StatusEffect {
  public BubbleStatusEffect() {
    super(StatusEffectCategory.BENEFICIAL,0x00ccff);
  }
  @Override
  public boolean canApplyUpdateEffect(int duration, int amplifier) {
    return true;
  }

  @Override
  public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    entity.fallDistance = 0.0f;
  }
}