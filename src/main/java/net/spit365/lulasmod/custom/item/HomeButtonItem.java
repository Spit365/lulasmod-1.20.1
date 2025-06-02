package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModMethods;

public class HomeButtonItem extends Item {
    public HomeButtonItem() {super(new Item.Settings().maxCount(1).maxDamage(100));}
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()) {
            player.getItemCooldownManager().set(this, 6000);
            ModMethods.sendHome(player, this);
            player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
