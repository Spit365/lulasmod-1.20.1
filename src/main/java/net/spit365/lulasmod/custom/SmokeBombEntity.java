package net.spit365.lulasmod.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.ModItems;
import net.spit365.lulasmod.mixin.ModEntities;

public class SmokeBombEntity extends ThrownItemEntity {
	public SmokeBombEntity(EntityType<? extends SmokeBombEntity> entityType, World world) {
		super(entityType, world);
	}

	public SmokeBombEntity(EntityType<? extends ThrownItemEntity> entityType, double d, double e, double f, World world) {
		super(entityType, d, e, f, world);
	}

	public SmokeBombEntity(World world, LivingEntity livingEntity) {
		super(ModEntities.SMOKE_BOMB, livingEntity, world);
	}

	@Override
	protected Item getDefaultItem() {
		return ModItems.SMOKE_BOMB;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		onCollision(entityHitResult);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {

			ServerWorld serverWorld = (ServerWorld) this.getWorld();
			serverWorld.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,hitResult.getPos().getX(),hitResult.getPos().getY() + 1,hitResult.getPos().getZ(),269, 1.2,1.2,1.2, 0);

		}
	}
}