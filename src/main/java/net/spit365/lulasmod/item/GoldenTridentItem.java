package net.spit365.lulasmod.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.custom.Impaled;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.util.ModUtil;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Item.Properties settings) {super(settings);}

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world.isClientSide() || !player.isCreative()) return InteractionResult.PASS;
        ItemStack item = player.getItemInHand(hand);
        if (Impaled.impale(player, ModUtil.selectClosestEntity(player, 5), item, Integer.MAX_VALUE, 5, ModParticles.GOLDEN_SHIMMER)) {
            player.getCooldowns().addCooldown(item, 200);
            return InteractionResult.SUCCESS;
        }
        player.getCooldowns().addCooldown(item, 20);
        return InteractionResult.PASS;
    }
}