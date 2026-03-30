package net.spit365.lulasmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.ModUtil;

public class SharpTomeItem extends Item{
    public SharpTomeItem(Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ItemStack paper = ModUtil.getInventoryStack(player, Items.PAPER);
        boolean requirePaper = player.isCreative(); // || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
        if (!world.isClient() && (paper != null || requirePaper)) {
            player.getItemCooldownManager().set(stack, 5);
            if (!requirePaper) paper.decrement(1);
            ArrowEntity arrow = new ArrowEntity(world, player, stack, paper);
            arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0f, 3f, 1f);
            world.spawnEntity(arrow);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}