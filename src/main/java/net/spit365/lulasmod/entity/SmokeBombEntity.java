package net.spit365.lulasmod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.mod.ModItems;

public class SmokeBombEntity extends ThrowableItemProjectile {
	public SmokeBombEntity(EntityType<? extends SmokeBombEntity> entityType, Level world) {super(entityType, world);}
	public SmokeBombEntity(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.SMOKE_BOMB, owner, world, stack);
        this.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0f, 1.5f, 0f);
    }

	@Override protected Item getDefaultItem() {return ModItems.SMOKE_BOMB;}
	@Override protected void onHitEntity(EntityHitResult entityHitResult) {collision(entityHitResult.getLocation());}
    @Override protected void onHitBlock(BlockHitResult blockHitResult) {collision(blockHitResult.getLocation().add(blockHitResult.getDirection().getUnitVec3()));}

    private void collision(Vec3 pos) {
		if (this.level() instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                pos.x(), pos.y(), pos.z(),
                269, 1.2d, 1.2d, 1.2d, 0.0d
            );
            this.level().playSound(
                null,
                pos.x(),
                pos.y(),
                pos.z(),
                getSound(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
            );
            this.discard();
        }
	}

    protected SoundEvent getSound() {
        return SoundEvents.SPLASH_POTION_BREAK;
    }
}
