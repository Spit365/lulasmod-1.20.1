package net.spit365.lulasmod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.mod.ModDamageTypes;
import net.spit365.lulasmod.mod.ModEntities;

public class AmethystShardEntity extends Projectile {
    public AmethystShardEntity(EntityType<? extends Projectile> entityType, Level world) {
        super(entityType, world);
    }
    public AmethystShardEntity(LivingEntity owner, Level world) {
        super(ModEntities.AMETHYST_SHARD, world);
        this.setPos(owner.getEyePosition());
        this.setOwner(owner);
        this.setDeltaMovement(owner.getViewVector(1).normalize().scale(0.5));
        this.setRot(owner.getYRot(), owner.getXRot());
        this.yRotO = owner.getYRot();
        this.xRotO = owner.getXRot();
    }

    @Override protected void onHitBlock(BlockHitResult hitResult) {
        for (int i = 0; i < 8; i++) if (this.level() instanceof ServerLevel sw) sw.sendParticles(
            new ItemParticleOption(ParticleTypes.ITEM, Items.AMETHYST_BLOCK),
            hitResult.getLocation().x() + random.nextGaussian() / 20,
            hitResult.getLocation().y() + random.nextGaussian() / 20,
            hitResult.getLocation().z() + random.nextGaussian() / 20,
            1,
            random.nextGaussian() / 20,
            0.2 + random.nextGaussian() / 20,
            random.nextGaussian() / 20,
            0.1
        );
        super.onHitBlock(hitResult);
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(1f), LivingEntity::isAlive))
            this.onHitEntity(new EntityHitResult(livingEntity));
        this.level().playSound(this, this.getX(), this.getY(), this.getZ(), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.NEUTRAL);
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        if (level() instanceof ServerLevel serverWorld) {
            Entity target = entityHitResult.getEntity();
            Entity owner = this.getOwner();
            DamageSource damageSource;
            if (owner != null) {
                if (owner.equals(target)) return;
                damageSource = ModDamageTypes.createDamageSource(owner, ModDamageTypes.AMETHYST_SHARD);
                if (owner instanceof LivingEntity livingEntity) livingEntity.setLastHurtMob(target);
            } else damageSource = ModDamageTypes.createDamageSource(this, ModDamageTypes.AMETHYST_SHARD);
            if (target.hurtServer(serverWorld, damageSource, 8) &&
                target instanceof LivingEntity livingEntity &&
                owner instanceof LivingEntity
            ) EnchantmentHelper.doPostAttackEffects(serverWorld, livingEntity, damageSource);
            serverWorld.playSound(null, BlockPos.containing(entityHitResult.getLocation()), SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH, SoundSource.NEUTRAL, 1.0f, 1.5f);
        }
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        Vec3 currentPos = this.position();
        Vec3 velocity = this.getDeltaMovement();
        Vec3 nextPos = currentPos.add(velocity);
        BlockHitResult blockHitResult = this.level()
            .clipIncludingBorder(
                new ClipContext(currentPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)
            );
        if (!blockHitResult.getType().equals(HitResult.Type.MISS)) {
            onHitBlock(blockHitResult);
            this.remove(RemovalReason.DISCARDED);
        }
        else this.setPos(nextPos);
        EntityHitResult entityCollision = ProjectileUtil.getEntityHitResult(this.level(), this, currentPos, nextPos, this.getBoundingBox().expandTowards(velocity).inflate(1.0), this::canHitEntity);
        if (entityCollision != null) {
            onHitEntity(entityCollision);
            this.remove(RemovalReason.DISCARDED);
        }
        this.setDeltaMovement(velocity.scale(1.5));
    }
}