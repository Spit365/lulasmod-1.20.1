package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.custom.item.seal.AbstractSealItem;
import net.spit365.lulasmod.mod.ModMethods;

public class SinfulItem extends SwordItem {
     public SinfulItem() {super(ToolMaterials.NETHERITE, new Item.Settings().fireproof().attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.NETHERITE, 3, -2.4F)).maxCount(1).maxDamage(2500));}

     @Override
     public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
          ModMethods.applyBleed(target, 100 * (int)(
               attacker.getStackInHand(Hand.OFF_HAND).getItem() instanceof AbstractSealItem abstractSealItem &&
               abstractSealItem.canUse(attacker)?
                    abstractSealItem.efficiencyMultiplier() :
                    1
          ));
          if (!attacker.getCommandTags().contains("tailed")) stack.damage(1, attacker, attacker.getPreferredEquipmentSlot(stack));
          return true;
     }
}
