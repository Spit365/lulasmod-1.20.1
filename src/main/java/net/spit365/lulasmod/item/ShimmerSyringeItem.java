package net.spit365.lulasmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.mod.ModData;

public class ShimmerSyringeItem extends Item {
    public ShimmerSyringeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        Shimmer.Variant variant = stack.get(ModData.SHIMMER_VARIANT);
        stack.decrementUnlessCreative(1, user);
        user.setAttached(ModData.APPLIED_SHIMMER_VARIANT, variant);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS);
        return variant != null ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    @Override
    public Text getName(ItemStack stack) {
        String base = super.getTranslationKey();
        Shimmer.Variant variant = stack.get(ModData.SHIMMER_VARIANT);
        if(variant == null) return Text.translatable(base);
        else return Text.translatable(base + ".variant." + variant.name);
    }
}
