package net.spit365.lulasmod.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
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

public class AmethystShardEntity extends PersistentProjectileEntity {
    public AmethystShardEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AmethystShardEntity(LivingEntity owner, World world) {
        super(ModEntities.AMETHYST_SHARD, owner, world, ItemStack.EMPTY, null);
        this.setNoGravity(true);
        this.setVelocity(owner.getRotationVec(1).normalize().multiply(0.5));
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
        for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(1f), LivingEntity::isAlive))
            this.onEntityHit(new EntityHitResult(livingEntity));
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK, SoundCategory.NEUTRAL);
        this.discard();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(Items.AIR);
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
            if (target.damage(damageSource, 8) &&
                target instanceof LivingEntity livingEntity &&
                owner instanceof LivingEntity
            ) EnchantmentHelper.onTargetDamaged(serverWorld, livingEntity, damageSource);
            serverWorld.playSound(null, BlockPos.ofFloored(entityHitResult.getPos()), SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, SoundCategory.NEUTRAL, 1.0f, 1.5f);
            this.discard();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setVelocity(this.getVelocity().multiply(1.5d));
    }
}