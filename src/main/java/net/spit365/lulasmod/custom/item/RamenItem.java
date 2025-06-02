package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.Mod;

public class RamenItem extends Item {
     public RamenItem() {super(new Item.Settings().food(new FoodComponent.Builder().build()));}

     @Override
     public ItemStack finishUsing(ItemStack itemstack, World world, LivingEntity entity){
          ((ServerWorld)world).spawnParticles(Mod.Particles.EXPLOSION, entity.getX(), entity.getY() +1, entity.getZ(), 0, 0, 0, 0, 0);
          return itemstack;
     }
}
