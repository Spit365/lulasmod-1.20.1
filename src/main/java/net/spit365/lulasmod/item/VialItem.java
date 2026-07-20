package net.spit365.lulasmod.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.mod.ModDamageTypes;

public class VialItem extends PotionItem {
    public VialItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        PotionContents effects = stack.get(DataComponents.POTION_CONTENTS);
        stack.consume(1, user);
        world.playSound(null, user.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS);
        if (world instanceof ServerLevel serverWorld) user.hurtServer(serverWorld, ModDamageTypes.createDamageSource(world, ModDamageTypes.BLOODSUCKING), user.getMaxHealth() / 2);
        if (effects == null) return InteractionResult.PASS;
        effects.potion().ifPresent(potionRegistryEntry -> potionRegistryEntry.value().getEffects().forEach(statusEffectInstance -> {
            Holder<MobEffect> effectType = statusEffectInstance.getEffect();
            user.addEffect(new MobEffectInstance(
                effectType,
                !effectType.value().isInstantenous() ? -1 : 1,
                statusEffectInstance.getAmplifier(),
                false,
                false)
            );
        }));
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack stack) {
        PotionContents potionContentsComponent = stack.get(DataComponents.POTION_CONTENTS);
        MutableComponent name = Component.translatable(this.descriptionId);
        if (potionContentsComponent == null) return name;
        MutableComponent base = Component.empty();
        potionContentsComponent.getAllEffects().forEach(statusEffectInstance -> {
            if (!base.equals(Component.empty())) base.append(Component.literal(", "));
            MutableComponent translated = Component.translatable(statusEffectInstance.getEffect().value().getDescriptionId());
            if (!translated.equals(Component.empty())) base.append(translated);
        });
        if (base.equals(Component.empty())) return name;
        base.append(Component.literal(" - ")).append(name);
        return base;
    }
}
