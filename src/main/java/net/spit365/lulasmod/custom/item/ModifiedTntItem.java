package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ModifiedTntItem extends Item {
    public ModifiedTntItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()  && (player.experienceLevel > 0 || player.isCreative())){
            player.getItemCooldownManager().set(this, 20);
            TntEntity tnt = new TntEntity(world, player.getX(), player.getY() +1, player.getZ(), player);
            world.spawnEntity(tnt);
            tnt.setFuse(20);
            tnt.setVelocity(player.getRotationVec(1).normalize().multiply(2.5));
            if (!player.isCreative()) {
                player.addExperienceLevels(-1);
                player.getStackInHand(hand).decrement(1);
            }
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
