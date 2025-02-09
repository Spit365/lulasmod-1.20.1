package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public class ModParticles {

    public static final DefaultParticleType SCRATCH = FabricParticleTypes.simple(true);


    public static void init(){
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(Lulasmod.MOD_ID, "scratch"), SCRATCH);
        ParticleFactoryRegistry.getInstance().register(SCRATCH, SweepAttackParticle.Factory::new);
    }
}
