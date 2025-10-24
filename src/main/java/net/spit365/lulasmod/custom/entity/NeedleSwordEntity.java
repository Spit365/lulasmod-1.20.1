package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModItems;

public class NeedleSwordEntity extends PersistentProjectileEntity {
    private final ItemStack sword;

    public NeedleSwordEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        sword = new ItemStack(ModItems.NEEDLE_SWORD);
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public NeedleSwordEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, ItemStack sword) {
        super(type, owner, world, sword, sword);
        this.sword = sword;
        this.pickupType = PickupPermission.DISALLOWED;
    }

    public ItemStack getSword() {
        return sword;
    }
    public boolean shouldReturn() {
        return age > 15;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return sword;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (entity != this.getOwner() && entity.getWorld() instanceof ServerWorld serverWorld)
            entity.damage(serverWorld, ModDamageSources.needleSword(entity), 8f);
    }

    @Override protected void onBlockHit(BlockHitResult blockHitResult) {
        Vec3d dir = this.getPos().add(blockHitResult.getSide().getDoubleVector().multiply(0.5));
        this.setPos(dir.x, dir.y, dir.z);
        Entity entity = this.getOwner();
        if (entity != null) entity.addVelocity(dir.subtract(entity.getPos()).normalize().multiply(0.75));
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity != null)
            if (shouldReturn()) {
                Vec3d relativePos = entity.getEyePos().subtract(this.getPos());
                this.setVelocity(relativePos.normalize());
                if (relativePos.length() < 0.5) {
                    ((LivingEntity) entity).giveOrDropStack(sword);
                    if (this.getWorld() instanceof ServerWorld serverWorld)
                        this.kill(serverWorld);
                }
            } else
                this.setVelocity(entity.getRotationVec(1).normalize());
        super.tick();
    }
}
