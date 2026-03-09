package net.spit365.lulasmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.custom.Bleed;

public class SinfulItem extends SwordItem {
     public SinfulItem(Settings settings) {super(ToolMaterials.NETHERITE, settings);}

     @Override
     public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
		 int bleed = 1;
		 if (attacker.getStackInHand(Hand.OFF_HAND).getItem() instanceof SealItem sealItem && sealItem.canUse.test(attacker)) {
			 bleed = (int) sealItem.potencyMultiplier;
			 sealItem.consequences.accept(attacker, bleed * 20);
		 }
		 Bleed.apply(target, bleed * 100);
		 return true;
	 }
}
