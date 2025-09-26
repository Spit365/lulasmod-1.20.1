package net.spit365.lulasmod.custom.item;


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
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.mod.ModDataComponentTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SpellBookItem extends Item implements SpellHotbar {
     public SpellBookItem() {super(new Settings().maxCount(1));}
     @Override public List<Identifier> displayList(PlayerEntity player){
         ItemStack stack = (player.getMainHandStack().getItem().equals(this)? player.getMainHandStack() : player.getOffHandStack());
         return stack.get(ModDataComponentTypes.SPELL_BOOK_SPELLS);
     }

     @Override
     public void cycleList(PlayerEntity player) {
          ItemStack stack = (player.getMainHandStack().getItem().equals(this)? player.getMainHandStack() : player.getOffHandStack());
          List<Identifier> list = stack.get(ModDataComponentTypes.SPELL_BOOK_SPELLS);
          if (list != null && list.isEmpty()) {
              Collections.rotate(list, -1);
              stack.set(ModDataComponentTypes.SPELL_BOOK_SPELLS, list);
          }
     }
     @Override
     public ActionResult use(World world, PlayerEntity player, Hand hand){
          if (world instanceof ServerWorld){
               ItemStack spellbook = player.getStackInHand(hand);
               ItemStack spell = (hand.equals(Hand.MAIN_HAND)? player.getOffHandStack() : player.getMainHandStack());
               List<Identifier> list = spellbook.get(ModDataComponentTypes.SPELL_BOOK_SPELLS);
               if (list != null)
                   if (spell.getItem() instanceof SpellItem) {
                        Identifier id = Registries.ITEM.getId(spell.getItem());
                        list.add(id);
                        spellbook.set(ModDataComponentTypes.SPELL_BOOK_SPELLS, list);
                        spell.decrement(1);
                        return ActionResult.SUCCESS;
                   } else if (!list.isEmpty()) {
                        Identifier id = list.getFirst();
                        list.remove(id);
                        spellbook.set(ModDataComponentTypes.SPELL_BOOK_SPELLS, list);
                        player.giveItemStack(new ItemStack(Registries.ITEM.get(id)));
                       return ActionResult.SUCCESS;
                   }
          }
          return ActionResult.PASS;
     }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        Objects.requireNonNull(stack.get(ModDataComponentTypes.SPELL_BOOK_SPELLS)).forEach(id ->
                textConsumer.accept(Registries.ITEM.get(id).getName()));
    }
}
