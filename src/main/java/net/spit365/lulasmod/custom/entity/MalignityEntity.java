package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.spit365.lulasmod.mod.ModEntities;

public class MalignityEntity extends FireballEntity {
    private int explosionPower = 1;

    public MalignityEntity(EntityType<? extends FireballEntity> entityType, World world) {super(entityType, world);}
    public MalignityEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super(world, owner, velocity.getX(), velocity.getY(), velocity.getZ(), explosionPower);
        this.explosionPower = explosionPower;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        World world = this.getWorld();
        if (type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
            world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
        } else if (type == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.onBlockHit(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, world.getBlockState(blockPos)));
        }
        if (!world.isClient) {
            world.createExplosion(this.getOwner(), this.getX(), this.getY(), this.getZ(), (float) this.explosionPower, false, World.ExplosionSourceType.NONE);
            this.discard();
        }
    }
}
