package net.spit365.lulasmod.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;

import java.util.HashMap;
import java.util.Map;

public class Impaled {
    public static final HashMap<ImpaledContext, Integer> impaled = new HashMap<>();

    public static void tick() {
        for (Map.Entry<ImpaledContext, Integer> impaledEntry : impaled.entrySet()) {
            ImpaledContext context = impaledEntry.getKey();
            Integer counter = impaledEntry.getValue();
            LivingEntity victim = context.livingEntity();
            if (context.iterations() > 0 && victim.isAlive()) {
                if (victim instanceof EndermanEntity) victim.kill((ServerWorld) victim.getWorld());
                victim.setVelocity(0, 0, 0);
                if (counter >= context.intervalDuration()) {
                    double radius = 5;
                    Vec3d pos = new Vec3d(Math.random() * radius - radius / 2, Math.random() * radius - radius / 2, Math.random() * radius - radius / 2).normalize().multiply(radius).add(victim.getPos());
                    context.player().setAttached(ModData.DAMAGE_DELAY, 0);
                    victim.getWorld().spawnEntity(new ParticleProjectileEntity(
                        victim.getWorld(), context.player(), pos, pos.subtract(victim.getEyePos()).multiply(-0.5), context.particle()));
                    impaled.remove(context);
                    impaled.put(new ImpaledContext(context, context.iterations() - 1, context.intervalDuration()), 0);
                } else impaled.put(context, counter + 1);
            } else {
                impaled.remove(context);
                context.player().removeAttached(ModData.DAMAGE_DELAY);
                victim.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 50));
            }
        }
    }

    public static Boolean impale(PlayerEntity player, ItemStack item, Integer baseCooldown, Integer maxCooldown, Integer iterations, Integer intervalls, ParticleEffect particle) {
        player.getItemCooldownManager().set(item, 2);
        if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity selectedEntity && impaled.keySet().stream().noneMatch(impaledContext -> impaledContext.livingEntity().equals(selectedEntity))) {
            player.getItemCooldownManager().set(item, maxCooldown);
            selectedEntity.requestTeleport(selectedEntity.getX(), selectedEntity.getY() + 5, selectedEntity.getZ());
            impaled.put(new ImpaledContext(player, selectedEntity, particle, iterations, intervalls), 0);
            return true;
        } else player.getItemCooldownManager().set(item, baseCooldown);
        return false;
    }

    public record ImpaledContext(PlayerEntity player, LivingEntity livingEntity, ParticleEffect particle, Integer iterations, Integer intervalDuration) {
        public ImpaledContext(ImpaledContext context, Integer iterations, Integer intervals) {
            this(context.player(), context.livingEntity(), context.particle(), iterations, intervals);
        }
    }
}
