package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.Mod;
import net.spit365.lulasmod.mod.ModMethods;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Settings settings) {super(settings);}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        return  !world.isClient() &&
                player.isCreative() &&
                ModMethods.impale(player, this, 20, 200, Integer.MAX_VALUE, Mod.Particles.GOLDEN_SHIMMER)?
                    TypedActionResult.success(player.getStackInHand(hand)):
                    TypedActionResult.pass(player.getStackInHand(hand));
    }
}