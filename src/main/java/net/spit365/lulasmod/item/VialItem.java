package net.spit365.lulasmod.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModDamageTypes;

public class VialItem extends PotionItem {
    public VialItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        PotionContentsComponent effects = stack.get(DataComponentTypes.POTION_CONTENTS);
        stack.decrementUnlessCreative(1, user);
        world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.PLAYERS);
        if (world instanceof ServerWorld) user.damage(ModDamageTypes.createDamageSource(world, ModDamageTypes.BLOODSUCKING), user.getMaxHealth() / 2);
        if (effects == null) return TypedActionResult.pass(stack);
        effects.potion().ifPresent(potionRegistryEntry -> potionRegistryEntry.value().getEffects().forEach(statusEffectInstance -> {
            RegistryEntry<StatusEffect> effectType = statusEffectInstance.getEffectType();
            user.addStatusEffect(new StatusEffectInstance(
                effectType,
                !effectType.value().isInstant() ? -1 : 1,
                statusEffectInstance.getAmplifier(),
                false,
                false)
            );
        }));
        return TypedActionResult.success(stack);
    }

    @Override
    public Text getName(ItemStack stack) {
        PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
        MutableText name = Text.translatable(this.getTranslationKey());
        if (potionContentsComponent == null) return name;
        MutableText base = Text.empty();
        potionContentsComponent.getEffects().forEach(statusEffectInstance -> {
            if (!base.equals(Text.empty())) base.append(Text.literal(", "));
            MutableText translated = Text.translatable(statusEffectInstance.getEffectType().value().getTranslationKey());
            if (!translated.equals(Text.empty())) base.append(translated);
        });
        if (base.equals(Text.empty())) return name;
        base.append(Text.literal(" - ")).append(name);
        return base;
    }
}
