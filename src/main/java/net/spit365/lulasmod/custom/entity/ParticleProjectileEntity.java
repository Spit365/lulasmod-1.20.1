package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModParticles;

public class ParticleProjectileEntity extends PersistentProjectileEntity {
    private int lifeTime = 0;
    private  int bleedBuildup = 0;
    private final ParticleEffect particleEffect;

    public ParticleProjectileEntity(EntityType<? extends ParticleProjectileEntity> type, World world, ParticleEffect particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, world);
        this.particleEffect = particleEffect;
    }

    public ParticleProjectileEntity(World world, LivingEntity owner, ParticleEffect particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, owner, world);
        this.particleEffect = particleEffect;
    }
    public ParticleProjectileEntity(World world, LivingEntity owner, Integer bleedBuildup) {
        super(ModEntities.PARTICLE_PROJECTILE, owner, world);
        this.bleedBuildup = bleedBuildup;
        this.particleEffect = ModParticles.CURSED_BLOOD;
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.lifeTime = nbt.getInt("Lifetime");
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Lifetime", this.lifeTime);
    }

    @Override
    public void tick(){
        super.tick();
        if (this.getWorld() instanceof ServerWorld) {
            ((ServerWorld) this.getWorld()).spawnParticles(particleEffect, this.getX(), this.getY(), this.getZ(), 2, 0.0625, 0.0625, 0.0625, 0);
            this.lifeTime++;
            if (this.lifeTime >= 600) this.discard();
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (this.particleEffect == ModParticles.CURSED_BLOOD && entity instanceof LivingEntity) {
            ModMethods.applyBleed((LivingEntity) entity, bleedBuildup);
        }
    }

    @Override protected ItemStack asItemStack() {return ItemStack.EMPTY;}
}