package net.spit365.lulasmod.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.item.SealItem;

@FunctionalInterface
public interface Spell {
    int cast(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownMultiplier);

    default int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, float potencyMultiplier, int cooldownDivisor) {
        return SealItem.FAIL_RESULT;
    }

    default int castTick(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
        return SealItem.FAIL_RESULT;
    }

    default int castStop(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
        return SealItem.FAIL_RESULT;
    }
}
