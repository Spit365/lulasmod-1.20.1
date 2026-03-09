package net.spit365.lulasmod.custom;

import net.minecraft.entity.Entity;
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Impaled {

    private static final Set<ImpaledContext> IMPALED = new HashSet<>();
    public static final int RADIUS = 5;

    public static void tick() {
        Iterator<ImpaledContext> it = IMPALED.iterator();
        while (it.hasNext()) {
            ImpaledContext ctx = it.next();
            LivingEntity victim = ctx.target;
            if (ctx.iterations > 0 && victim.isAlive()) {
                if (victim instanceof EndermanEntity enderman) {
                    enderman.kill();
                    cleanup(ctx, it);
                    continue;
                }
                victim.setVelocity(0, 0, 0);
                ctx.counter++;
                if (ctx.counter >= ctx.intervalDuration) {
                    ctx.counter = 0;
                    ctx.iterations--;
                    Vec3d pos = Vec3d.fromPolar(
                        (float) (Math.random() * 360),
                        (float) (Math.random() * 180 - 90)
                    ).normalize().multiply(RADIUS).add(victim.getPos());
                    ctx.attacker.setAttached(ModData.DAMAGE_DELAY, 0);
                    victim.getWorld().spawnEntity(
                        new ParticleProjectileEntity(
                            victim.getWorld(),
                            ctx.attacker,
                            pos,
                            pos.subtract(victim.getEyePos()).multiply(-0.5),
                            ctx.particle
                        )
                    );
                    if (ctx.iterations <= 0) cleanup(ctx, it);
                }
            } else cleanup(ctx, it);
        }
    }

    private static void cleanup(ImpaledContext ctx, Iterator<ImpaledContext> it) {
        it.remove();
        ctx.attacker.removeAttached(ModData.DAMAGE_DELAY);
        ctx.target.addStatusEffect(
            new StatusEffectInstance(StatusEffects.SLOW_FALLING, RADIUS * 10)
        );
    }

    public static boolean impale(PlayerEntity attacker, Entity target, ItemStack stack, int iterations, int intervalDuration, ParticleEffect particle) {
        attacker.getItemCooldownManager().set(stack.getItem(), 2);
        if (!(target instanceof LivingEntity living) || IMPALED.stream().anyMatch(ctx -> ctx.attacker == attacker || ctx.target == living)) {
            return false;
        }
        living.requestTeleport(living.getX(), living.getY() + RADIUS, living.getZ());
        IMPALED.add(new ImpaledContext(attacker, living, particle, iterations, intervalDuration));
        return true;
    }

    public static final class ImpaledContext {
        private final PlayerEntity attacker;
        private final LivingEntity target;
        private final ParticleEffect particle;
        private final int intervalDuration;
        private int iterations;
        private int counter = 0;

        public ImpaledContext(PlayerEntity attacker, LivingEntity target, ParticleEffect particle, int iterations, int intervalDuration) {
            this.attacker = attacker;
            this.target = target;
            this.particle = particle;
            this.iterations = iterations;
            this.intervalDuration = intervalDuration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ImpaledContext other)) return false;
            return attacker == other.attacker || target == other.target;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(attacker) ^ System.identityHashCode(target);
        }
    }
}
