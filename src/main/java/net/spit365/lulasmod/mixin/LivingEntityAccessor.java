package net.spit365.lulasmod.mixin;

import org.spongepowered.asm.mixin.gen.Accessor;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.entity.LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    boolean isJumping();
}
