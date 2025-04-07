package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.mod.ModImportant;
import net.spit365.lulasmod.mod.ModItems;

public class BlackFlameEntity extends ThrownItemEntity {

	public BlackFlameEntity(EntityType<? extends BlackFlameEntity> entityType, World world) {
		super(entityType, world);
	}

	public BlackFlameEntity(World world, LivingEntity owner) {
		super(ModEntities.BLACK_FLAME, owner, world);
	}


	@Override
	protected Item getDefaultItem() {
		return ModItems.BLACK_FLAME_INCANTATION;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		if (!this.getWorld().isClient) {
			this.getWorld().createExplosion(this, entityHitResult.getPos().getX(), entityHitResult.getPos().getY(), entityHitResult.getPos().getZ(), 2, World.ExplosionSourceType.NONE);
			if (entityHitResult.getEntity() instanceof LivingEntity) entityHitResult.getEntity().damage(this.getWorld().getDamageSources().inFire(), (float) ((LivingEntity) entityHitResult.getEntity()).defaultMaxHealth / 10);
			this.getWorld().playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.ENTITY_ENDERMAN_SCREAM,
					SoundCategory.PLAYERS,
					1.0F,
					1.0F
			);
			this.discard();
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);

		if (!this.getWorld().isClient) {
			getWorld().createExplosion(this, hitResult.getPos().getX(), hitResult.getPos().getY(), hitResult.getPos().getZ(), 2, World.ExplosionSourceType.NONE);
			ModImportant.summonSmoke(hitResult.getPos(), this.getWorld());

			// Play sound
			this.getWorld().playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.BLOCK_CAMPFIRE_CRACKLE,
					SoundCategory.NEUTRAL,
					1.0F,
					1.0F
			);
			this.discard();
		}
	}
}
