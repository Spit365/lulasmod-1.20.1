package net.spit365.lulasmod.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SmokeBombEntity extends ThrownItemEntity {

	public SmokeBombEntity(EntityType<? extends SmokeBombEntity> entityType, World world) {
		super(entityType, world);
	}

	public SmokeBombEntity(World world, LivingEntity owner) {
		super(ModEntities.SMOKE_BOMB, owner, world);
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.SMOKE_BOMB;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		ModImportant.summonSmoke(entityHitResult.getPos(),entityHitResult.getEntity().getWorld());
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);

		if (!this.getWorld().isClient) {
			ModImportant.summonSmoke(hitResult.getPos(), this.getWorld());

			// Play sound
			this.getWorld().playSound(
					null,
					this.getX(),
					this.getY(),
					this.getZ(),
					SoundEvents.ENTITY_SPLASH_POTION_BREAK,
					SoundCategory.NEUTRAL,
					1.0F,
					1.0F
			);

			// Remove the entity
			this.discard();
		}
	}
}
