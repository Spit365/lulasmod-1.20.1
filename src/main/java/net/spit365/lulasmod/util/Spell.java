package net.spit365.lulasmod.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.spit365.lulasmod.item.SealItem;

@FunctionalInterface
public interface Spell {
    int cast(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownMultiplier);

    default int hitEntity(ServerLevel world, Player player, InteractionHand hand, LivingEntity target, float potencyMultiplier, int cooldownDivisor) {
        return SealItem.FAIL_RESULT;
    }

    default int castTick(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
        return SealItem.FAIL_RESULT;
    }

    default int castStop(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
        return SealItem.FAIL_RESULT;
    }
}
