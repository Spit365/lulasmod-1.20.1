package net.spit365.lulasmod.item;


import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.SpellHotbar;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SpellBookItem extends Item implements SpellHotbar {
     public SpellBookItem(Settings settings) {super(settings);}
     @Override public List<Identifier> getHotbarList(PlayerEntity player){
         ItemStack stack = (player.getMainHandStack().getItem().equals(this)? player.getMainHandStack() : player.getOffHandStack());
         return stack.get(ModData.SPELL_BOOK_SPELLS);
     }

     @Override
     public void onCycle(PlayerEntity player) {
          ItemStack stack = (player.getMainHandStack().getItem().equals(this)? player.getMainHandStack() : player.getOffHandStack());
		 List<Identifier> mutable = ModMethods.makeMutable(stack.get(ModData.SPELL_BOOK_SPELLS));
          if (mutable.isEmpty()) {
              Collections.rotate(mutable, -1);
              stack.set(ModData.SPELL_BOOK_SPELLS, mutable);
          }
     }
     @Override
     public ActionResult use(World world, PlayerEntity player, Hand hand){
          if (world instanceof ServerWorld){
               ItemStack spellbook = player.getStackInHand(hand);
               ItemStack spell = (hand.equals(Hand.MAIN_HAND)? player.getOffHandStack() : player.getMainHandStack());
			   List<Identifier> mutable = ModMethods.makeMutable(spellbook.get(ModData.SPELL_BOOK_SPELLS));
			   if (spell.getItem() instanceof SpellItem) {
				   Identifier id = Registries.ITEM.getId(spell.getItem());
				   mutable.add(id);
				   spellbook.set(ModData.SPELL_BOOK_SPELLS, mutable);
				   spell.decrement(1);
				   return ActionResult.SUCCESS;
			   } else if (!mutable.isEmpty()) {
				   Identifier id = mutable.getFirst();
				   mutable.remove(id);
				   spellbook.set(ModData.SPELL_BOOK_SPELLS, mutable);
				   player.giveItemStack(new ItemStack(Registries.ITEM.get(id)));
				   return ActionResult.SUCCESS;
			   }

          }
          return ActionResult.PASS;
     }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
		List<Identifier> spells = stack.get(ModData.SPELL_BOOK_SPELLS);
		if (spells != null && !spells.isEmpty()) spells.forEach(id ->
                textConsumer.accept(Registries.ITEM.get(id).getName()));
    }
}
