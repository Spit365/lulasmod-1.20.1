package net.spit365.lulasmod.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.entity.SmokeBombEntity;

public class SmokeBombItem extends Item {
    public SmokeBombItem(Settings settings) {super(settings);}

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        ItemStack stackInHand = player.getStackInHand(hand);
        if (!world.isClient()){
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            SmokeBombEntity smokeBombEntity = new SmokeBombEntity(world, player, stackInHand);
            smokeBombEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 0.0F);
            world.spawnEntity(smokeBombEntity);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 400, 0, false, true));
            if (!player.isCreative()) stackInHand.decrement(1);
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}