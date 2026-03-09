package net.spit365.lulasmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.Impaled;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.util.ModUtil;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Item.Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient() || !player.isCreative()) return TypedActionResult.pass(stack);
        if (Impaled.impale(player, ModUtil.selectClosestEntity(player, 5), stack, Integer.MAX_VALUE, 5, ModParticles.GOLDEN_SHIMMER)) {
            player.getItemCooldownManager().set(stack.getItem(), 200);
            return TypedActionResult.success(stack);
        }
        player.getItemCooldownManager().set(stack.getItem(), 20);
        return TypedActionResult.pass(stack);
    }
}