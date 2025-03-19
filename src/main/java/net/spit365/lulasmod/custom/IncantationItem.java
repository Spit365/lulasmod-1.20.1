package net.spit365.lulasmod.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModItems;

public class IncantationItem extends Item {
    public IncantationItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && player.getItemCooldownManager().isCoolingDown(this)) {
            player.getItemCooldownManager().set(this, 5);
            SpellManager.addSpell(player, player.getStackInHand(hand));
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
