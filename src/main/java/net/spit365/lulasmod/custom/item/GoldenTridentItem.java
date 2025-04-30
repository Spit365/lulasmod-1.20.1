package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModParticles;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Settings settings) {
        super(settings);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()) {
            if (
                ModMethods.a(player, this, 20, 200, 5d, 0, 5d, 0.5d, ModParticles.GOLDEN_SHIMMER)
            ) return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
