package net.spit365.lulasmod.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.ModUtil;

public class HomeButtonItem extends Item {
    public HomeButtonItem(Settings settings) {super(settings);}
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient()) {
            player.getItemCooldownManager().set(stack.getItem(), 6000);
            ModUtil.sendHome(player, this);
			stack.damage(1, player, switch (hand) {
                case MAIN_HAND -> EquipmentSlot.MAINHAND;
                case OFF_HAND -> EquipmentSlot.OFFHAND;
            });
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }
}
