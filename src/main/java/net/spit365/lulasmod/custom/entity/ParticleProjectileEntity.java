package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.item.seal.BloodsuckingSeal;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.mod.ModStatusEffects;

public class ParticleProjectileEntity extends PersistentProjectileEntity {

    private final ParticleEffect particleEffect;

    public ParticleProjectileEntity(EntityType<? extends ParticleProjectileEntity> type, World world, ParticleEffect particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, world);
        this.particleEffect = particleEffect;
    }

    public ParticleProjectileEntity(World world, LivingEntity owner, ParticleEffect particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, owner, world);
        this.particleEffect = particleEffect;
    }

    @Override
    public void tick(){
        super.tick();
        if (this.getWorld() instanceof ServerWorld) ((ServerWorld) this.getWorld()).spawnParticles(particleEffect, this.getX(), this.getY(), this.getZ(), 2, 0.0625, 0.0625, 0.0625, 0);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (this.particleEffect == ModParticles.CURSED_BLOOD && entity instanceof LivingEntity) {
            BloodsuckingSeal.applyBleed((LivingEntity) entity, 200);
        }
    }

    @Override protected ItemStack asItemStack() {return ItemStack.EMPTY;}
}