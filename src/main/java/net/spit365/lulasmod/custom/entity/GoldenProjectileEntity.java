package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;

public class GoldenProjectileEntity extends PersistentProjectileEntity {

    public GoldenProjectileEntity(EntityType<? extends GoldenProjectileEntity> type, World world) {
        super(ModEntities.GOLDEN_PROJECTILE, world);
    }

    public GoldenProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.GOLDEN_PROJECTILE, owner, world);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }
}
