package net.spit365.lulasmod.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class CushionedStatusEffect extends MobEffect {
    public CushionedStatusEffect() {super(MobEffectCategory.BENEFICIAL,0x00ccff);}

    @Override public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {return true;}
    @Override public boolean applyEffectTick(ServerLevel world, LivingEntity entity, int amplifier) {entity.fallDistance = 0.0f; return true;}
}