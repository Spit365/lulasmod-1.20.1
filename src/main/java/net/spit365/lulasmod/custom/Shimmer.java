package net.spit365.lulasmod.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.packet.MindShimmerS2CPacket;

public final class Shimmer {
    @Environment(EnvType.CLIENT)
    public static boolean mindShimmerEnabled = false;

    public static void tick(ServerPlayer player) {
        Variant variant = player.getAttached(ModData.APPLIED_SHIMMER_VARIANT);
        ServerPlayNetworking.send(player, new MindShimmerS2CPacket(variant == Variant.MIND));
        if (!player.isAlive()) return;
        if (variant == Variant.REFORMATION) {
            float health = player.getHealth();
            if (health == 1) {
                player.removeAttached(ModData.APPLIED_SHIMMER_VARIANT);
                player.setHealth(player.getMaxHealth());
            } else if (health < player.getMaxHealth() / 2 && !player.hasEffect(MobEffects.REGENERATION)) player.addEffect(
                new MobEffectInstance(MobEffects.REGENERATION, 80, 2, true, false));
        } else if (variant == Variant.FORTITUDE) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 1, 0, true, false));
        }
    }

    public enum Variant {
        FORTITUDE("fortitude"),
        PACE("pace"),
        MIND("mind"),
        REFORMATION("reformation"),;

        public final String name;

        Variant(String name) {
            this.name = name;
        }

        public static Variant byIndex(int index) {
            return values()[index];
        }

        public int getIndex() {
            return ordinal();
        }
    }
}
