package net.spit365.lulasmod.custom.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.server.world.ServerWorld;

public class CushionedStatusEffect extends StatusEffect {
    public CushionedStatusEffect() {super(StatusEffectCategory.BENEFICIAL,0x00ccff);}

    @Override public boolean canApplyUpdateEffect(int duration, int amplifier) {return true;}
    @Override public boolean applyUpdateEffect(ServerWorld world, LivingEntity entity, int amplifier) {entity.fallDistance = 0.0f; return true;}
}