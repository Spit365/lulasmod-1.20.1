package net.spit365.lulasmod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SmokeProjectileEntity extends SmokeBombEntity{
    public SmokeProjectileEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(world, owner, stack);
    }

    public SmokeProjectileEntity(EntityType<? extends SmokeBombEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.BREEZE_CHARGE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverWorld) serverWorld.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0d, 0d, 0d, 0d);
    }
}
