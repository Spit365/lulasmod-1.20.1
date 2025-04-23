package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public class ModParticles {

    public static final DefaultParticleType SCRATCH = register("scratch", true, SweepAttackParticle.Factory::new);
    public static final DefaultParticleType GOLDEN_SHIMMER = register("golden_shimmer", false, FlameParticle.Factory::new);


    public static void init(){}

    private static DefaultParticleType register(String name, Boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> render){
            DefaultParticleType particle = FabricParticleTypes.simple(alwaysShow);
            Registry.register(Registries.PARTICLE_TYPE, new Identifier(Lulasmod.MOD_ID, name), particle);
            ParticleFactoryRegistry.getInstance().register(particle, render);
            return particle;
    }
}