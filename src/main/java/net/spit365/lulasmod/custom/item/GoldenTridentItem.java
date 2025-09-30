package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModParticles;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Item.Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        return  !world.isClient() &&
                player.isCreative() &&
                ModMethods.impale(player, player.getStackInHand(hand), 20, 200, Integer.MAX_VALUE, 5, ModParticles.GOLDEN_SHIMMER)?
                    ActionResult.SUCCESS:
                    ActionResult.PASS;
    }
}