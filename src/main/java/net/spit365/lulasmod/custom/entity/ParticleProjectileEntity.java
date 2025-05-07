package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;
import org.jetbrains.annotations.Nullable;

public class ParticleProjectileEntity extends PersistentProjectileEntity {
    private int lifeTime = 0;
    private final ParticleEffect particleEffect;

    public ParticleProjectileEntity(EntityType<? extends ParticleProjectileEntity> ignoredType, World world) {
        super(ModEntities.PARTICLE_PROJECTILE, world);
        this.particleEffect = null;
    }

    public ParticleProjectileEntity(World world, LivingEntity owner, @Nullable ParticleEffect particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, owner, world);
        this.particleEffect = particleEffect;
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
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            if (particleEffect != null) serverWorld.spawnParticles(particleEffect, this.getX(), this.getY(), this.getZ(), 2, 0.0625, 0.0625, 0.0625, 0);
            this.lifeTime++;
            if (this.lifeTime >= 600) this.discard();
        }
    }

    @Override protected boolean canHit(Entity entity) {return !(entity instanceof PlayerEntity player && player.isCreative());}
    @Override protected ItemStack asItemStack() {return ItemStack.EMPTY;}
}