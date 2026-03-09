package net.spit365.lulasmod.entity;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModEntities;
import org.jetbrains.annotations.Nullable;

public class ParticleProjectileEntity extends PersistentProjectileEntity {
    private int lifeTime = 0;
    private ParticleEffect particleEffect;

    public ParticleProjectileEntity(EntityType<? extends ParticleProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.particleEffect = null;
    }

    public ParticleProjectileEntity(World world, LivingEntity owner, Vec3d pos, Vec3d velocity, @Nullable ParticleEffect particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, owner, world, ItemStack.EMPTY, null);
        this.particleEffect = particleEffect;
        this.setPos(pos.getX(), pos.getY(), pos.getZ());
        this.setVelocity(velocity);
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

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.lifeTime = nbt.getInt("lifeTime");
        String particle = nbt.getString("particle");
        if (particle == null || particle.isBlank()) return;
        try {
            this.particleEffect = ParticleEffectArgumentType.readParameters(new StringReader(particle), this.getWorld().getRegistryManager());
        } catch (CommandSyntaxException cse) {
            Lulasmod.LOGGER.warn("Couldn't load custom particle {}: {}", particle, cse);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("lifeTime", this.lifeTime);
        if (this.particleEffect != null) nbt.putString("particle", this.particleEffect.toString());
        return nbt;
    }

    @Override protected boolean canHit(Entity entity) {return true;}

    @Override protected ItemStack getDefaultItemStack() {return ItemStack.EMPTY;}

    @Override public boolean hasNoGravity() {return true;}
}