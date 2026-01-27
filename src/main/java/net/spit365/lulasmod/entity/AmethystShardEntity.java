package net.spit365.lulasmod.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModDamageTypes;
import net.spit365.lulasmod.mod.ModEntities;

public class AmethystShardEntity extends ProjectileEntity {
    public AmethystShardEntity(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
    public AmethystShardEntity(LivingEntity owner, World world) {
        super(ModEntities.AMETHYST_SHARD, world);
        this.setPosition(owner.getEyePos());
        this.setOwner(owner);
        this.setVelocity(owner.getRotationVec(1).normalize().multiply(3));
        this.setRotation(owner.getYaw(), owner.getPitch());
        this.lastYaw = owner.getYaw();
        this.lastPitch = owner.getPitch();
    }

    @Override protected void onBlockHit(BlockHitResult hitResult) {
        for (int i = 0; i < 8; i++) if (this.getWorld() instanceof ServerWorld sw) sw.spawnParticles(
            new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.AMETHYST_BLOCK, 1)),
            hitResult.getPos().getX() + random.nextGaussian() / 20,
            hitResult.getPos().getY() + random.nextGaussian() / 20,
            hitResult.getPos().getZ() + random.nextGaussian() / 20,
            1,
            random.nextGaussian() / 20,
            0.2 + random.nextGaussian() / 20,
            random.nextGaussian() / 20,
            0.1
        );
        super.onBlockHit(hitResult);
        for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(1f), LivingEntity::isAlive))
            this.onEntityHit(new EntityHitResult(livingEntity));
        this.getWorld().playSound(this, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.NEUTRAL);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (getWorld() instanceof ServerWorld serverWorld) {
            Entity target = entityHitResult.getEntity();
            Entity owner = this.getOwner();
            DamageSource damageSource;
            if (owner != null) {
                if (owner.equals(target)) return;
                damageSource = ModDamageTypes.createDamageSource(owner, ModDamageTypes.AMETHYST_SHARD);
                if (owner instanceof LivingEntity livingEntity) livingEntity.onAttacking(target);
            } else damageSource = ModDamageTypes.createDamageSource(this, ModDamageTypes.AMETHYST_SHARD);
            if (target.damage(serverWorld, damageSource, 8) &&
                target instanceof LivingEntity livingEntity &&
                owner instanceof LivingEntity
            ) EnchantmentHelper.onTargetDamaged(serverWorld, livingEntity, damageSource);
            serverWorld.playSound(null, BlockPos.ofFloored(entityHitResult.getPos()), SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, SoundCategory.NEUTRAL, 1.0f, 1.5f);
        }
    }

    @Override protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        Vec3d currentPos = this.getPos();
        Vec3d nextPos = currentPos.add(this.getVelocity());
        BlockHitResult blockHitResult = this.getWorld()
            .getCollisionsIncludingWorldBorder(
                new RaycastContext(currentPos, nextPos, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this)
            );
        if (!blockHitResult.getType().equals(HitResult.Type.MISS)){
            onBlockHit(blockHitResult);
            this.remove(RemovalReason.DISCARDED);
        }
        else this.setPosition(nextPos);
        EntityHitResult entityCollision = ProjectileUtil.getEntityCollision(this.getWorld(), this, currentPos, nextPos, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), this::canHit);
        if (entityCollision != null) {
            onEntityHit(entityCollision);
            this.remove(RemovalReason.DISCARDED);
        }
    }
}