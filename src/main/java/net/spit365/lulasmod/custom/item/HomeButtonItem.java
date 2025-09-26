package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModMethods;

public class HomeButtonItem extends Item {
    public HomeButtonItem() {super(new Item.Settings().maxCount(1).maxDamage(100));}
    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()) {
			ItemStack stack = player.getStackInHand(hand);
            player.getItemCooldownManager().set(stack, 6000);
            ModMethods.sendHome(player, this);
			stack.damage(1, player, hand);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
