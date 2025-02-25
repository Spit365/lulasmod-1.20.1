package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class ModImportant {
    public static void summonSmoke(@NotNull Vec3d position, World world){
        ((ServerWorld) world).spawnParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                position.x, position.y + 1.0d, position.z,
                269, 1.2d, 1.2d, 1.2d, 0.0d
        );
    }
    public static void summonSmoke(@NotNull Entity entity){
        ((ServerWorld) entity.getWorld()).spawnParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                entity.getX(), entity.getY() + 1, entity.getZ(),
                269, 1.2, 1.2, 1.2, 0
        );
    }
    public static boolean creeperExplode = false;
    public static String whoExplode = "MEGAMASTER75983";
}
