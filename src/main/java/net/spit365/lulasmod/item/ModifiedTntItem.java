package net.spit365.lulasmod.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ModifiedTntItem extends Item {
    public ModifiedTntItem(Properties settings) {super(settings);}

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()  && (player.experienceLevel > 0 || player.isCreative())) {
			ItemStack stack = player.getItemInHand(hand);
            player.getCooldowns().addCooldown(stack, 20);
            PrimedTnt tnt = new PrimedTnt(world, player.getX(), player.getY() +1, player.getZ(), player);
            tnt.setFuse(20);
            tnt.setDeltaMovement(player.getViewVector(1).normalize().scale(2.5));
            world.addFreshEntity(tnt);
            if (!player.isCreative()) {
                player.giveExperienceLevels(-1);
				stack.shrink(1);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
