package net.spit365.lulasmod.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spit365.lulasmod.item.ShimmerSyringeItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.packet.MindShimmerS2CPacket;

public class Shimmer {
    @Environment(EnvType.CLIENT)
    public static boolean mindShimmerEnabled = false;

    public static void tick(ServerPlayerEntity player) {
        ShimmerSyringeItem.Variant variant = player.getAttached(ModData.APPLIED_SHIMMER_VARIANT);
        ServerPlayNetworking.send(player, new MindShimmerS2CPacket(variant == ShimmerSyringeItem.Variant.MIND));
        switch (variant) {
            case REFORMATION -> {
                if (!player.isAlive()) return;
                float health = player.getHealth();
                if (health < 2) {
                    player.removeAttached(ModData.APPLIED_SHIMMER_VARIANT);
                    player.setHealth(player.getMaxHealth());
                } else if (health < player.getMaxHealth() / 2 && !player.hasStatusEffect(StatusEffects.REGENERATION)) player.addStatusEffect(
                    new StatusEffectInstance(StatusEffects.REGENERATION, 80, 2, true, false));
            }
            case null, default -> {}
        }
    }
}
