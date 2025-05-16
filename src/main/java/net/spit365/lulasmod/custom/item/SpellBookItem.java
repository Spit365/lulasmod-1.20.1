package net.spit365.lulasmod.custom.item;


import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SpellHotbar;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class SpellBookItem extends Item implements SpellHotbar {
     public SpellBookItem() {super(new Settings().maxCount(1));}
     @Override public LinkedList<Identifier> display(PlayerEntity player){
          ItemStack stack = (player.getMainHandStack().getItem().equals(this)? player.getMainHandStack() : player.getOffHandStack());
          NbtCompound nbt = stack.getOrCreateNbt();
          Lulasmod.LOGGER.info(nbt.toString());
          return getListFromString(nbt.getString("Spells"));
     }

     @Override
     public void cycle(PlayerEntity player) {
          ItemStack stack = (player.getMainHandStack().getItem().equals(this)? player.getMainHandStack() : player.getOffHandStack());
          NbtCompound nbt = stack.getOrCreateNbt();
          LinkedList<Identifier> list = getListFromString(nbt.getString("Spells"));
          if (!list.isEmpty()) {
               Identifier first = list.pollFirst();
               list.addLast(first);
               stack.setNbt(nbt);
               nbt.putString("Spells", getStringFromList(list));
          }
     }
     //TODO: Fucking fix whatever this shi is
     @Override
     public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
//          if (world instanceof ServerWorld){
//               ItemStack stack = (hand.equals(Hand.MAIN_HAND)? player.getOffHandStack() : player.getMainHandStack());
//               NbtCompound nbt = stack.getOrCreateNbt();
//               if (stack.getItem() instanceof SpellItem spellItem) {
//                    Identifier id = Registries.ITEM.getId(spellItem);
//                    LinkedList<Identifier> list = getListFromString(nbt.getString("Spells"));
//                    if (!list.contains(id)) list.add(id);
//                    nbt.putString("Spells", getStringFromList(list));
//                    stack.setNbt(nbt);
//                    stack.decrement(1);
//                    return TypedActionResult.success(player.getStackInHand(hand));
//               } else if (stack.getItem().equals(Items.AIR)){
//                    LinkedList<Identifier> list = getListFromString(nbt.getString("Spells"));
//                    if (!list.isEmpty()) {
//                         Identifier id = list.pollFirst();
//                         list.remove(id);
//                         stack.setNbt(nbt);
//                         nbt.putString("Spells", getStringFromList(list));
//                         player.giveItemStack(new ItemStack(Registries.ITEM.get(id)));
//                         return TypedActionResult.success(player.getStackInHand(hand));
//                    }
//               }
//          }
          if(player.getStackInHand(hand).hasNbt()) {
               player.getStackInHand(hand).setNbt(new NbtCompound());
          }
          return TypedActionResult.success(player.getStackInHand(hand));
     }

     @Override
     public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
          if(stack.hasNbt()) {
               assert stack.getNbt() != null;
               String currentOre = stack.getNbt().getString("Spells");
               tooltip.add(Text.literal(currentOre));
          }
     }

     private static LinkedList<Identifier> getListFromString(String s){
          LinkedList<Identifier> list = new LinkedList<>();
          for (String s1 : s.split(";")){

               String[] s2 = s1.split(":");
               if (s2.length == 2) list.add(new Identifier(s2[0], s2[1]));
          }
          return list;
     }
     private static String getStringFromList(LinkedList<Identifier> list){
          StringBuilder stringBuilder = new StringBuilder();
          for (Identifier id : list){
               stringBuilder.append(";").append(id);
          }
          return stringBuilder.toString();
     }
}
