package net.spit365.lulasmod.item;

import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModifiedTntItem extends Item {
    public ModifiedTntItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        ItemStack stack = player.getStackInHand(hand);
        if (!world.isClient()  && (player.experienceLevel > 0 || player.isCreative())){
            player.getItemCooldownManager().set(stack.getItem(), 20);
            TntEntity tnt = new TntEntity(world, player.getX(), player.getY() +1, player.getZ(), player);
            tnt.setFuse(20);
            tnt.setVelocity(player.getRotationVec(1).normalize().multiply(2.5));
            world.spawnEntity(tnt);
            if (!player.isCreative()) {
                player.addExperienceLevels(-1);
				stack.decrement(1);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(stack);
        }
        return TypedActionResult.pass(stack);
    }
}
