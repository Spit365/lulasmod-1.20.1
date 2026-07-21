package net.spit365.lulasmod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class MalignityEntity extends LargeFireball {
    private int explosionPower = 1;

    public MalignityEntity(EntityType<? extends LargeFireball> entityType, Level world) {super(entityType, world);}
    public MalignityEntity(Level world, LivingEntity owner, Vec3 velocity, int explosionPower) {
        super(world, owner, velocity, explosionPower);
        this.setPos(owner.getEyePosition());
        this.explosionPower = explosionPower;
    }

    @Override
    public void tick() {
        this.setDeltaMovement(this.getDeltaMovement().scale(1.125));
        super.tick();
    }

    @Override
    protected void onHit(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        Level world = this.level();
        if (type == HitResult.Type.ENTITY) {
            this.onHitEntity((EntityHitResult)hitResult);
            world.gameEvent(GameEvent.PROJECTILE_LAND, hitResult.getLocation(), GameEvent.Context.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.onHitBlock(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            world.gameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Context.of(this, world.getBlockState(blockPos)));
        }
        if (!world.isClientSide()) {
            world.explode(this.getOwner(), this.getX(), this.getY(), this.getZ(), (float) this.explosionPower, false, Level.ExplosionInteraction.NONE);
            this.discard();
        }
    }
}
