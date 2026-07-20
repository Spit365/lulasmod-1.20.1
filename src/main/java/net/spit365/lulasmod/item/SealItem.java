package net.spit365.lulasmod.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.item.spell.SorceryItem;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.SpellHotbar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class SealItem extends Item implements SpellHotbar {
    public SealItem(Properties settings, Predicate<LivingEntity> canUse, Consequences consequences, float potencyMultiplier, int cooldownDivisor) {
        super(settings);
        this.canUse = canUse;
        this.consequences = consequences;
        this.potencyMultiplier = potencyMultiplier;
        this.cooldownDivisor = cooldownDivisor;
    }

    public SealItem(Properties settings, Predicate<LivingEntity> canUse, float potencyMultiplier, int cooldownDivisor) {
        this(settings, canUse, (entity, cooldown) -> {}, potencyMultiplier, cooldownDivisor);
    }

    public static final int NO_COOLDOWN_RESULT = 0;
    public static final int FAIL_RESULT = -1;
    public final Predicate<LivingEntity> canUse;
    public final Consequences consequences;
    public final float potencyMultiplier;
    public final int cooldownDivisor;


    @Override
    public List<ResourceLocation> getHotbarList(Player player) {
        return player.getAttached(ModData.EQUIPPED_SPELLS);
    }

    @Override
    public void onCycle(Player player,  Function<List<ResourceLocation>, List<ResourceLocation>> cycleFunction) {
        player.setAttached(ModData.EQUIPPED_SPELLS, cycleFunction.apply(player.getAttached(ModData.EQUIPPED_SPELLS)));
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        sealLogic(null, null, attacker, getHandFromStack(attacker, stack), (serverWorld, player, hand, spellItem) ->
            spellItem.spell.hitEntity(serverWorld, player, hand, target, potencyMultiplier, cooldownDivisor));
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        return sealLogic(InteractionResult.SUCCESS, InteractionResult.PASS, player, hand, (serverWorld, ignored1, ignored2, spellItem) ->
            spellItem.spell.cast(serverWorld, player, hand, potencyMultiplier, cooldownDivisor));
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        sealLogic(null, null, user, getHandFromStack(user, stack), (serverWorld, player, hand, spellItem) ->
            spellItem.spell.castTick(serverWorld, player, hand, potencyMultiplier, cooldownDivisor));
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        return sealLogic(true, false, user, getHandFromStack(user, stack), (serverWorld, player, hand, spellItem) ->
            spellItem.spell.castStop(serverWorld, player, hand, potencyMultiplier, cooldownDivisor));
    }

    private <T> T sealLogic(T resultSuccess, T resultFail, LivingEntity user, InteractionHand hand, SpellAction spellAction) {
        if (!(user instanceof Player player) || !(player.level() instanceof ServerLevel serverWorld) || !canUse.test(player))
            return resultFail;
        List<ResourceLocation> spellList = player.getAttached(ModData.EQUIPPED_SPELLS);
        if (spellList == null || spellList.isEmpty() || !(BuiltInRegistries.ITEM.getValue(spellList.getFirst()) instanceof SpellItem spellItem))
            return resultFail;

        int result = spellAction.accept(serverWorld, player, hand, spellItem);
        if (result >= 0) consequences.accept(player, result);
        return switch (result) {
            case NO_COOLDOWN_RESULT -> resultSuccess;
            case FAIL_RESULT -> resultFail;
            default -> {
                if (result > 0) {
                    player.getCooldowns().addCooldown(player.getItemInHand(hand), Math.max(result, 2));
                    yield resultSuccess;
                }
                throw new IllegalArgumentException("Result integer from Spell must be >= -1, got " + result);
            }
        };
    }

    private static @NotNull InteractionHand getHandFromStack(LivingEntity user, ItemStack stack) {
        return Arrays.stream(InteractionHand.values()).filter(hand -> user.getItemInHand(hand).equals(stack)).findFirst().orElse(InteractionHand.MAIN_HAND);
    }

    @FunctionalInterface
    private interface SpellAction {
        int accept(ServerLevel serverWorld, Player player, InteractionHand hand, SpellItem spellItem);
    }

    @FunctionalInterface
    public interface Consequences {
        void accept(LivingEntity entity, int cooldown);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player && !Objects.equals(ModUtil.getInventoryStack(player, stack.getItem()), stack)) {
            player.displayClientMessage(Component.translatable("notify.lulasmod.duplicate_seal"), true);
            entity.spawnAtLocation(world, stack.copy());
            stack.shrink(stack.getCount());
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        List<ResourceLocation> spells = user.getAttached(ModData.EQUIPPED_SPELLS);
        return spells != null && BuiltInRegistries.ITEM.getValue(spells.getFirst()) instanceof SorceryItem ? 72000 : 0;
    }
}