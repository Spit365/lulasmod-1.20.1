package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.spell.SpellManager;

public class MemoryStoneItem extends Item {
    public MemoryStoneItem(Settings settings) {super(settings);}
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !player.getItemCooldownManager().isCoolingDown(this)){
            player.getItemCooldownManager().set(this, 5);
            SpellManager.addPlayerSpellSlots(player, 1);
            if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}