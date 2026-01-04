package net.spit365.lulasmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;

import java.util.Arrays;

public class SinfulItem extends Item {
     public SinfulItem(Settings settings) {super(settings);}

     @Override
     public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
		 int bleed = 1;
		 if (attacker.getStackInHand(Hand.OFF_HAND).getItem() instanceof SealItem sealItem && sealItem.canUse.test(attacker)) {
			 bleed = (int) sealItem.potencyMultiplier;
			 sealItem.consequences.accept(attacker, bleed * 20);
		 }
		 Bleed.apply(target, bleed * 100);
     }
}
