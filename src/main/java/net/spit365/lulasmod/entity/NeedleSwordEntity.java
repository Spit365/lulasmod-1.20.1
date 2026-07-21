package net.spit365.lulasmod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.mod.ModDamageTypes;
import net.spit365.lulasmod.mod.ModItems;

public class NeedleSwordEntity extends AbstractArrow {
    private final ItemStack sword;

    public NeedleSwordEntity(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
        sword = new ItemStack(ModItems.NEEDLE_SWORD);
        this.pickup = Pickup.DISALLOWED;
    }

    public NeedleSwordEntity(EntityType<? extends AbstractArrow> type, LivingEntity owner, Level world, ItemStack sword) {
        super(type, owner, world, sword, sword);
        this.sword = sword;
        this.pickup = Pickup.DISALLOWED;
    }

    public ItemStack getSword() {
        return sword;
    }
    public boolean shouldReturn() {
        return tickCount > 15;
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return sword;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        Entity entity = entityHitResult.getEntity();
        if (takeDamage(entity) && entity.level() instanceof ServerLevel serverWorld)
            entity.hurtServer(serverWorld, ModDamageTypes.createDamageSource(entity, ModDamageTypes.NEEDLE_SWORD), 8f);
    }

    @Override protected void onHitBlock(BlockHitResult blockHitResult) {
        Vec3 dir = this.position().add(blockHitResult.getDirection().getUnitVec3().scale(0.5));
        this.setPosRaw(dir.x, dir.y, dir.z);
        Entity entity = this.getOwner();
        if (entity != null) entity.push(dir.subtract(entity.position()).normalize().scale(0.75));
    }

    private boolean takeDamage(Entity entity) {
        Entity thisOwner = this.getOwner();
        if (entity.equals(thisOwner) && thisOwner instanceof LivingEntity livingEntity) return !livingEntity.getMainHandItem().is(Items.AIR);
        return true;
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (entity != null) {
            if (shouldReturn()) {
                Vec3 relativePos = entity.getEyePosition().subtract(this.position());
                this.setDeltaMovement(relativePos.normalize());
                if (relativePos.length() < 0.5) {
                    ((LivingEntity) entity).handleExtraItemsCreatedOnUse(sword);
                    if (this.level() instanceof ServerLevel serverWorld) this.kill(serverWorld);
                }
            } else this.setDeltaMovement(entity.getViewVector(1).normalize());
        }
        super.tick();
    }
}
