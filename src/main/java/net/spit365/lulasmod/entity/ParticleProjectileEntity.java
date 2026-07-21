package net.spit365.lulasmod.entity;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModEntities;
import org.jetbrains.annotations.Nullable;

public class ParticleProjectileEntity extends AbstractArrow {
    private int lifeTime = 0;
    private ParticleOptions particleEffect;

    public ParticleProjectileEntity(EntityType<? extends ParticleProjectileEntity> entityType, Level world) {
        super(entityType, world);
        this.particleEffect = null;
    }

    public ParticleProjectileEntity(Level world, LivingEntity owner, Vec3 pos, Vec3 velocity, @Nullable ParticleOptions particleEffect) {
        super(ModEntities.PARTICLE_PROJECTILE, owner, world, ItemStack.EMPTY, null);
        this.particleEffect = particleEffect;
        this.setPosRaw(pos.x(), pos.y(), pos.z());
        this.setDeltaMovement(velocity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverWorld) {
            if (particleEffect != null) serverWorld.sendParticles(particleEffect, this.getX(), this.getY(), this.getZ(), 2, 0.0625, 0.0625, 0.0625, 0);
            this.lifeTime++;
            if (this.lifeTime >= 600) this.discard();
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        this.lifeTime = view.getIntOr("Lifetime", 0);
        String particle = view.getStringOr("Particle", "");
        if (!particle.isEmpty()) {
            try {
                this.particleEffect = ParticleArgument.readParticle(new StringReader(particle), this.level().registryAccess());
            } catch (CommandSyntaxException var5) {
                Lulasmod.LOGGER.warn("Couldn't load custom particle {}: {}", particle, var5);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        view.putInt("Lifetime", this.lifeTime);
        if (this.particleEffect != null) view.putString("Particle", this.particleEffect.toString());
    }

    @Override protected boolean canHitEntity(Entity entity) {return true;}

    @Override protected ItemStack getDefaultPickupItem() {return ItemStack.EMPTY;}

    @Override public boolean isNoGravity() {return true;}
}