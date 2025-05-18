package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.Mod;

public class SmokeBombEntity extends ThrownItemEntity {
	public SmokeBombEntity(EntityType<? extends SmokeBombEntity> entityType, World world) {super(entityType, world);}
	public SmokeBombEntity(World world, LivingEntity owner) {super(Mod.Entities.SMOKE_BOMB, owner, world);}

	@Override protected Item getDefaultItem() {return Mod.Items.SMOKE_BOMB;}
	@Override protected void onEntityHit(EntityHitResult entityHitResult) {collision(entityHitResult.getPos());}
	@Override protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		collision(hitResult.getPos());
	}

	private void collision(Vec3d pos) {
		if (this.getWorld() instanceof ServerWorld serverWorld) {
				serverWorld.spawnParticles(
					ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
					pos.getX(), pos.getY() + 1.0d, pos.getZ(),
					269, 1.2d, 1.2d, 1.2d, 0.0d
				);
				this.getWorld().playSound(
						null,
						pos.getX(),
						pos.getY(),
						pos.getZ(),
						SoundEvents.ENTITY_SPLASH_POTION_BREAK,
						SoundCategory.NEUTRAL,
						1.0F,
						1.0F
				);
				this.discard();
			}
	}
}
