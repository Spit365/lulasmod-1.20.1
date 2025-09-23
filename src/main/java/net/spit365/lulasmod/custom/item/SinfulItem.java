package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.custom.item.seal.AbstractSealItem;
import net.spit365.lulasmod.mod.ModMethods;

import java.util.Arrays;

public class SinfulItem extends Item {
     public SinfulItem() {super(new Settings().sword(ToolMaterial.NETHERITE, 3, -2.4F).fireproof().maxCount(1).maxDamage(2500));}


     @Override
     public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
          ModMethods.applyBleed(target, 100 * (int)(
               attacker.getStackInHand(Hand.OFF_HAND).getItem() instanceof AbstractSealItem abstractSealItem &&
               abstractSealItem.canUse(attacker)?
                    abstractSealItem.efficiencyMultiplier() :
                    1
          ));
          if (!attacker.getCommandTags().contains("tailed")) stack.damage(1, attacker, Arrays.stream(Hand.values()).filter(hand -> attacker.getStackInHand(hand).equals(stack)).toArray(Hand[]::new)[0]);
     }
}
