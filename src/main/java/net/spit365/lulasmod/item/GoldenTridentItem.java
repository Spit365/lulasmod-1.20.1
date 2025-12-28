package net.spit365.lulasmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.TridentItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.Impaled;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModParticles;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Item.Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        return  !world.isClient() &&
                player.isCreative() &&
                Impaled.impale(player, ModMethods.selectClosestEntity(player, 5), player.getStackInHand(hand), 20, 200, Integer.MAX_VALUE, 5, ModParticles.GOLDEN_SHIMMER)?
                    ActionResult.SUCCESS:
                    ActionResult.PASS;
    }
}