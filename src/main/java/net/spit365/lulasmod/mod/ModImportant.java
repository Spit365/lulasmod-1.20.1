package net.spit365.lulasmod.mod;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import org.jetbrains.annotations.NotNull;

public class ModImportant {
    public static final String PocketDimensionKey = "ResourceKey[minecraft:dimension / " + Lulasmod.MOD_ID + ":pocket_dimension]";

    public static void summonSmoke(@NotNull Vec3d position, World world){
        ((ServerWorld) world).spawnParticles(
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                position.x, position.y + 1.0d, position.z,
                269, 1.2d, 1.2d, 1.2d, 0.0d
        );
    }
}
