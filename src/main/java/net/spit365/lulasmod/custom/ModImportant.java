package net.spit365.lulasmod.custom;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ModImportant {
    public static void summonSmoke(Vec3d position, World world){
        ((ServerWorld) world).spawnParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                position.x, position.y + 1, position.z,
                269, 1.2, 1.2, 1.2, 0
        );
    }
    public static boolean creeperExplode = false;
}
