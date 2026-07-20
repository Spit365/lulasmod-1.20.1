package net.spit365.lulasmod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.mod.ModData;

public class ShimmerSyringeItem extends Item {
    public ShimmerSyringeItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        Shimmer.Variant variant = stack.get(ModData.SHIMMER_VARIANT);
        stack.consume(1, user);
        user.setAttached(ModData.APPLIED_SHIMMER_VARIANT, variant);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS);
        return variant != null ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public Component getName(ItemStack stack) {
        String base = super.getDescriptionId();
        Shimmer.Variant variant = stack.get(ModData.SHIMMER_VARIANT);
        if(variant == null) return Component.translatable(base);
        else return Component.translatable(base + ".variant." + variant.name);
    }
}
