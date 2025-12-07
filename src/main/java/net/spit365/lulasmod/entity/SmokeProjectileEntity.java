package net.spit365.lulasmod.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;

public class SmokeProjectileEntity extends SmokeBombEntity{
    public SmokeProjectileEntity(World world, LivingEntity owner, ItemStack stack) {
        super(world, owner, stack);
    }

    public SmokeProjectileEntity(EntityType<? extends SmokeBombEntity> entityType, World world) {
        super(ModEntities.SMOKE_PROJECTILE, world);
    }

    @Override
    protected SoundEvent getSound() {
        return SoundEvents.ENTITY_BREEZE_CHARGE;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld() instanceof ServerWorld serverWorld) serverWorld.spawnParticles(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), 0, 0d, 0d, 0d, 0d);
    }
}
