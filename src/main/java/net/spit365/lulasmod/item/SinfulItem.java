package net.spit365.lulasmod.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.mod.ModMethods;

import java.util.Arrays;

public class SinfulItem extends Item {
     public SinfulItem(Settings settings) {super(settings);}

     @Override
     public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
          ModMethods.applyBleed(target, 100 * (int)(
               attacker.getStackInHand(Hand.OFF_HAND).getItem() instanceof SealItem sealItem &&
               sealItem.canUse.accept(attacker)?
                    sealItem.efficiencyMultiplier :
                    1
          ));
          if (!attacker.getCommandTags().contains("tailed")) stack.damage(1, attacker, Arrays.stream(Hand.values()).filter(hand -> attacker.getStackInHand(hand).equals(stack)).toArray(Hand[]::new)[0]);
     }
}
