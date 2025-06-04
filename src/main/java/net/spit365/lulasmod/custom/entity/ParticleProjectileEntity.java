package net.spit365.lulasmod.custom.entity;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.command.argument.ParticleEffectArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.Mod;
import org.jetbrains.annotations.Nullable;

public class ParticleProjectileEntity extends PersistentProjectileEntity {
    private int lifeTime = 0;
    private ParticleEffect particleEffect;

    public ParticleProjectileEntity(EntityType<? extends ParticleProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.particleEffect = null;
    }

    public ParticleProjectileEntity(World world, LivingEntity owner, Vec3d pos, Vec3d velocity, @Nullable ParticleEffect particleEffect) {
        super(Mod.Entities.PARTICLE_PROJECTILE, owner, world);
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
        this.lifeTime = nbt.getInt("Lifetime");
        if (nbt.contains("Particle", NbtElement.STRING_TYPE)) {
            try {
                this.particleEffect = ParticleEffectArgumentType.readParameters(new StringReader(nbt.getString("Particle")), Registries.PARTICLE_TYPE.getReadOnlyWrapper());
            } catch (CommandSyntaxException var5) {
                Lulasmod.LOGGER.warn("Couldn't load custom particle {}", nbt.getString("Particle"), var5);
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Lifetime", this.lifeTime);
        if (this.particleEffect != null) nbt.putString("Particle", this.particleEffect.asString());
    }

    @Override protected boolean canHit(Entity entity) {return true;}
    @Override protected ItemStack asItemStack() {return ItemStack.EMPTY;}
    @Override public boolean hasNoGravity() {return true;}
}