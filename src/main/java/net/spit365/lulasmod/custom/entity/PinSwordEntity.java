package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModItems;

public class PinSwordEntity extends PersistentProjectileEntity {
    private final ItemStack sword;

    public PinSwordEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
        sword = new ItemStack(ModItems.PIN_SWORD);
    }

    public PinSwordEntity(EntityType<? extends PersistentProjectileEntity> type, LivingEntity owner, World world, ItemStack sword) {
        super(type, owner, world, sword, sword);
        this.sword = sword;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return sword;
    }

    @Override
    public void tick() {
        if (this.age <= 30){
            this.setVelocity(this.getRotationVec(1));
        } else if (this.getOwner() instanceof LivingEntity livingEntity){
            Vec3d relativePos = livingEntity.getEyePos().subtract(this.getPos());
            if (relativePos.length() < 0.5){
                if (this.getWorld() instanceof ServerWorld serverWorld) this.kill(serverWorld);
                livingEntity.giveOrDropStack(sword);
                return;
            }
            this.setVelocity(relativePos);
        }
    }
}
