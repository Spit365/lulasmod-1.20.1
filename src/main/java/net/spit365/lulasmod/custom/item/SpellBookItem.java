package net.spit365.lulasmod.custom.item;


import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.SpellHotbar;

import java.util.LinkedList;
import java.util.List;

public class SpellBookItem extends Item implements SpellHotbar {
     public SpellBookItem() {
          super(new Settings().maxCount(1));
     }

     @Override
     public LinkedList<Identifier> displayList(PlayerEntity player) {
          ItemStack stack = player.getMainHandStack().getItem().equals(this)
                  ? player.getMainHandStack()
                  : player.getOffHandStack();
          NbtCompound nbt = getOrCreateCustomNbt(stack);
          return getListFromString(nbt.getString("Spells"));
     }

     @Override
     public void cycleList(PlayerEntity player) {
          ItemStack stack = player.getMainHandStack().getItem().equals(this)
                  ? player.getMainHandStack()
                  : player.getOffHandStack();
          NbtCompound nbt = getOrCreateCustomNbt(stack);
          LinkedList<Identifier> list = getListFromString(nbt.getString("Spells"));

          if (!list.isEmpty()) {
               Identifier first = list.pollFirst();
               list.addLast(first);
               nbt.putString("Spells", getStringFromList(list));
               setCustomNbt(stack, nbt);
          }
     }

     @Override
     public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
          if (world instanceof ServerWorld) {
               ItemStack spellbook = player.getStackInHand(hand);
               ItemStack spell = hand.equals(Hand.MAIN_HAND) ? player.getOffHandStack() : player.getMainHandStack();
               NbtCompound nbt = getOrCreateCustomNbt(spellbook);
               LinkedList<Identifier> list = getListFromString(nbt.getString("Spells"));
               if (spell.getItem() instanceof SpellItem) {
                    Identifier id = Registries.ITEM.getId(spell.getItem());
                    list.add(id);
                    nbt.putString("Spells", getStringFromList(list));
                    setCustomNbt(spellbook, nbt);
                    spell.decrement(1);
                    return TypedActionResult.success(spellbook);
               } else if (!list.isEmpty()) {
                    Identifier id = list.pollFirst();
                    list.remove(id);
                    nbt.putString("Spells", getStringFromList(list));
                    setCustomNbt(spellbook, nbt);
                    player.giveItemStack(new ItemStack(Registries.ITEM.get(id)));
                    return TypedActionResult.success(spellbook);
               }
          }

          return TypedActionResult.pass(player.getStackInHand(hand));
     }

     @Override
     public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
          NbtCompound nbt = getOrCreateCustomNbt(stack);
          getListFromString(nbt.getString("Spells")).forEach(id ->
                  tooltip.add(Registries.ITEM.get(id).getName()));
     }

     private static LinkedList<Identifier> getListFromString(String s) {
          LinkedList<Identifier> list = new LinkedList<>();
          for (String s1 : s.split(", ")) {
               if (s1.split(":").length == 2) {
                    list.add(Identifier.of(s1));
               }
          }
          return list;
     }

     private static String getStringFromList(LinkedList<Identifier> list) {
          StringBuilder stringBuilder = new StringBuilder();
          for (Identifier id : list) {
               if (!stringBuilder.isEmpty()) stringBuilder.append(", ");
               stringBuilder.append(id);
          }
          return stringBuilder.toString();
     }

     private static NbtCompound getOrCreateCustomNbt(ItemStack stack) {
          NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
          return nbtComponent != null ? nbtComponent.copyNbt() : new NbtCompound();
     }

     private static void setCustomNbt(ItemStack stack, NbtCompound tag) {
          stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(tag));
     }
}