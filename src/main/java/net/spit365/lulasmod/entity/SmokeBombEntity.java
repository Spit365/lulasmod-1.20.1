package net.spit365.lulasmod.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.mod.ModItems;

public class SmokeBombEntity extends ThrownItemEntity {
	public SmokeBombEntity(EntityType<? extends SmokeBombEntity> entityType, World world) {super(entityType, world);}
	public SmokeBombEntity(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.SMOKE_BOMB, owner, world, stack);
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0f, 1.5f, 0f);
    }

	@Override protected Item getDefaultItem() {return ModItems.SMOKE_BOMB;}
	@Override protected void onEntityHit(EntityHitResult entityHitResult) {collision(entityHitResult.getPos());}
    @Override protected void onBlockHit(BlockHitResult blockHitResult) {collision(blockHitResult.getPos().add(blockHitResult.getSide().getDoubleVector()));}

    private void collision(Vec3d pos) {
		if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                pos.getX(), pos.getY(), pos.getZ(),
                269, 1.2d, 1.2d, 1.2d, 0.0d
            );
            this.getWorld().playSound(
                null,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                getSound(),
                SoundCategory.PLAYERS,
                1.0F,
                1.0F
            );
            this.discard();
        }
	}

    protected SoundEvent getSound() {
        return SoundEvents.ENTITY_SPLASH_POTION_BREAK;
    }
}
