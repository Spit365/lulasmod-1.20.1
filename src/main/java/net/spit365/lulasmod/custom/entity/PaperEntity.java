package net.spit365.lulasmod.custom.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class PaperEntity extends PersistentProjectileEntity {

    public PaperEntity(EntityType<? extends PersistentProjectileEntity> type, World world) {
        super(type,  world);
    }

    @Override
    public double getDamage() {
        return 4;
    }

    @Override
    protected ItemStack asItemStack() {
        return Items.PAPER.getDefaultStack();
    }
}