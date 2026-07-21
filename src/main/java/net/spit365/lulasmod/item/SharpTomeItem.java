package net.spit365.lulasmod.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.util.ModUtil;

public class SharpTomeItem extends Item{
    public SharpTomeItem(Properties settings) {super(settings);}

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack paper = ModUtil.getInventoryStack(player, Items.PAPER);
        boolean requirePaper = player.isCreative(); // || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
        if (!world.isClientSide() && (paper != null || requirePaper)) {
            player.getCooldowns().addCooldown(stack, 5);
            if (!requirePaper) paper.shrink(1);
            Arrow arrow = new Arrow(world, player, stack, paper);
            arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0f, 3f, 1f);
            world.addFreshEntity(arrow);
            player.awardStat(Stats.ITEM_USED.get(this));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}