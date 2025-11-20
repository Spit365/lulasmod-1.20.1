package net.spit365.lulasmod.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModDamageSources;

public class VialItem extends PotionItem {
    public VialItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        PotionContentsComponent effects = stack.get(DataComponentTypes.POTION_CONTENTS);
        stack.decrementUnlessCreative(1, user);
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS);
        if (world instanceof ServerWorld serverWorld) user.damage(serverWorld, ModDamageSources.bloodsucking(user), user.getMaxHealth() / 2);
        if (effects == null) return ActionResult.PASS;
        effects.potion().ifPresent(potionRegistryEntry -> potionRegistryEntry.value().getEffects().forEach(statusEffectInstance ->
            user.addStatusEffect(new StatusEffectInstance(statusEffectInstance.getEffectType(), -1))));
        return ActionResult.SUCCESS;
    }

    @Override
    public Text getName(ItemStack stack) {
        PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContentsComponent == null) return super.getName();
        MutableText base = Text.empty();
        potionContentsComponent.getEffects().forEach(statusEffectInstance -> {
            if (!base.equals(Text.empty())) base.append(Text.literal(", "));
            base.append(Text.translatable(statusEffectInstance.getEffectType().value().getTranslationKey()));
        });
        base.append(Text.literal(" - ")).append(Text.translatable(this.translationKey));
        return base;
    }
}
