package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class GoldenSeal extends AbstractSealItem {
     @Override
     public Boolean canUse(LivingEntity entity) {
          if (entity instanceof ServerPlayerEntity player && !player.isCreative()) {
               if (player.experienceLevel <= 0 && player.experienceProgress <= 0f) return false;
               player.addExperience(-1);
          }
          return true;
     }
     @Override public Float efficiencyMultiplier() {
          return 1f;
     }
     @Override public Integer cooldownMultiplier() {
          return 1;
     }
}