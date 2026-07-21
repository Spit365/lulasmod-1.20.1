package net.spit365.lulasmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.mod.ModItems;

public class SinfulItem extends Item {
     public SinfulItem(Properties settings) {super(settings);}

     @Override
     public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		 int bleed = 1;
		 if (attacker.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof SealItem sealItem && sealItem.canUse.test(attacker)) {
			 bleed = (int) sealItem.potencyMultiplier;
			 sealItem.consequences.accept(attacker, bleed * 20);
		 }
		 Bleed.apply(target, bleed * 100);

	     if (Demon.isDemon(attacker)) stack.setDamageValue(0);
     }
}
