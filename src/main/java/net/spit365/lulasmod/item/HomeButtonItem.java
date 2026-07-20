package net.spit365.lulasmod.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.util.ModUtil;

public class HomeButtonItem extends Item {
    public HomeButtonItem(Properties settings) {super(settings);}
    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
			ItemStack stack = player.getItemInHand(hand);
            player.getCooldowns().addCooldown(stack, 6000);
            ModUtil.sendHome(player, this);
			stack.hurtAndBreak(1, player, hand);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
