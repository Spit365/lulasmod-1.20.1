package net.spit365.lulasmod.custom.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.Mod;

public class AmethystShardEntity extends PersistentProjectileEntity {
    public AmethystShardEntity(EntityType<? extends AmethystShardEntity> entityType, World world) {
        super(entityType, world);
        this.setSound(this.getHitSound());
        this.setDamage(8);
        this.pickupType = PickupPermission.DISALLOWED;
    }
    public AmethystShardEntity(LivingEntity owner, World world) {
        super(Mod.Entities.AMETHYST_SHARD, owner, world);
        this.setSound(this.getHitSound());
        this.setDamage(8);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    @Override protected ItemStack asItemStack() {return new ItemStack(Items.AIR);}
    @Override protected SoundEvent getHitSound() {return SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK;}
    @Override protected void onBlockHit(BlockHitResult hitResult) {
        for (int i = 0; i < 8; i++) if (this.getWorld() instanceof ServerWorld sw) sw.spawnParticles(
            new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.AMETHYST_BLOCK, 1)),
            this.getX() + random.nextGaussian() / 20,
            this.getY() + random.nextGaussian() / 20,
            this.getZ() + random.nextGaussian() / 20,
            1,
            random.nextGaussian() / 20,
            0.2 + random.nextGaussian() / 20,
            random.nextGaussian() / 20,
            0.1
        );
        super.onBlockHit(hitResult);
        for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(1f), LivingEntity::isAlive))
            this.onEntityHit(new EntityHitResult(livingEntity));
        this.remove(RemovalReason.DISCARDED);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource2;
         if (owner != null) {
             if (owner.equals(target)) return;
             damageSource2 = Mod.DamageSources.AMETHYST_SHARD(owner);
             if (owner instanceof LivingEntity livingEntity) livingEntity.onAttacking(target);
         } else damageSource2 = Mod.DamageSources.AMETHYST_SHARD(this);
         if (target.damage(damageSource2, (float) this.getDamage())) {
            if (target.getType().equals(EntityType.ENDERMAN)) return;
            if (target instanceof LivingEntity livingEntity) {
                if (!this.getWorld().isClient && owner instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(livingEntity, owner);
                    EnchantmentHelper.onTargetDamaged((LivingEntity) owner, livingEntity);
                }
                this.onHit(livingEntity);
                if (livingEntity != owner && livingEntity instanceof PlayerEntity && owner instanceof ServerPlayerEntity && !this.isSilent())
                    ((ServerPlayerEntity) owner).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
            }
        } else {
            this.setVelocity(this.getVelocity().multiply(-0.1d));
            this.setYaw(this.getYaw() + 180f);
            this.prevYaw += 180f;
        }
        this.getWorld().playSound(null, this.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT_SWEET_BERRY_BUSH, SoundCategory.NEUTRAL, 1.0f, 1.5f);
    }
}