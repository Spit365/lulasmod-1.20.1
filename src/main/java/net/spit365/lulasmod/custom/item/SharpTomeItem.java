package net.spit365.lulasmod.custom.item;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModMethods;

public class SharpTomeItem extends Item{
    public SharpTomeItem() {super(new Item.Settings().maxCount(1).maxDamage(640));}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        ItemStack paper = ModMethods.getItemStack(player, Items.PAPER);
        boolean requirePaper = player.isCreative() || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
        if (!world.isClient() && (paper != null || requirePaper)){
            player.getItemCooldownManager().set(this, 5);
            if (!requirePaper) paper.decrement(1);
            ArrowEntity arrow = new ArrowEntity(world, player);
            arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0f, 3f, 1f);
            if (EnchantmentHelper.getLevel(Enchantments.POWER, stack) > 0) arrow.setDamage(arrow.getDamage() + (double)EnchantmentHelper.getLevel(Enchantments.POWER, stack) * 0.5 + 0.5);
            if (EnchantmentHelper.getLevel(Enchantments.PUNCH, stack) > 0) arrow.setPunch(EnchantmentHelper.getLevel(Enchantments.PUNCH, stack));
            if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) arrow.setOnFireFor(100);
            world.spawnEntity(arrow);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }
}