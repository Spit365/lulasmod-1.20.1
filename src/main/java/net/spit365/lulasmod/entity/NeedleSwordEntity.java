package net.spit365.lulasmod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModDamageTypes;
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
        if (takeDamage(entity) && entity.getWorld() instanceof ServerWorld serverWorld)
            entity.damage(ModDamageTypes.createDamageSource(entity, ModDamageTypes.NEEDLE_SWORD), 8f);
    }

    @Override protected void onBlockHit(BlockHitResult blockHitResult) {
        Vec3d dir = this.getPos().add(Vec3d.of(blockHitResult.getSide().getVector()).multiply(0.5));
        this.setPos(dir.x, dir.y, dir.z);
        Entity entity = this.getOwner();
        if (entity != null) entity.addVelocity(dir.subtract(entity.getPos()).normalize().multiply(0.75));
    }

    private boolean takeDamage(Entity entity){
        Entity thisOwner = this.getOwner();
        if (entity.equals(thisOwner) && thisOwner instanceof LivingEntity livingEntity) return !livingEntity.getMainHandStack().isOf(Items.AIR);
        return true;
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity != null) {
            if (shouldReturn()) {
                Vec3d relativePos = entity.getEyePos().subtract(this.getPos());
                this.setVelocity(relativePos.normalize());
                if (relativePos.length() < 0.5) {
                    if (entity instanceof PlayerEntity player) player.getInventory().insertStack(sword);
                    this.kill();
                }
            } else this.setVelocity(entity.getRotationVec(1).normalize());
        }
        super.tick();
    }
}
