package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.custom.entity.AmethystShardEntity;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.entity.SmokeBombEntity;
import net.spit365.lulasmod.custom.entity.renderer.AmethystShardEntityRenderer;
import net.spit365.lulasmod.custom.entity.renderer.ParticleProjectileEntityRenderer;

public class ModClient {
     private static class register{
          private static <T extends Entity> EntityType<T> Entity(String id, EntityType.EntityFactory<T> entityFactory, EntityRendererFactory<T> entityRendererFactory, float width, float height, int maxTrackingRange, int trackingTickInterval) {
              RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Server.MOD_ID, id));
              EntityType<T> entityType = Registry.register(Registries.ENTITY_TYPE, key,
                       EntityType.Builder.create(entityFactory, SpawnGroup.MISC)
                               .dimensions(width, height)
                               .maxTrackingRange(maxTrackingRange)
                               .trackingTickInterval(trackingTickInterval)
                               .build(key));
               EntityRendererRegistry.register(entityType, entityRendererFactory);
               return entityType;
          }
          private static SimpleParticleType Particle(String name, Boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> render){
               SimpleParticleType particle = FabricParticleTypes.simple(alwaysShow);
               Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Server.MOD_ID, name), particle);
               ParticleFactoryRegistry.getInstance().register(particle, render);
               return particle;
          }
     }
     public static class Entities {
          public static final EntityType<SmokeBombEntity> SMOKE_BOMB = register.<SmokeBombEntity>Entity(
                  "smoke_bomb",
                  SmokeBombEntity::new,
                  FlyingItemEntityRenderer::new,
                  0.25F, 0.25F, 4, 10);
          public static final EntityType<MalignityEntity> MALIGNITY = register.<MalignityEntity>Entity(
                  "malignity",
                  MalignityEntity::new,
                  FlyingItemEntityRenderer::new,
                  1.0F, 1.0F, 4, 10);
          public static final EntityType<ParticleProjectileEntity> PARTICLE_PROJECTILE = register.<ParticleProjectileEntity>Entity(
                  "particle_projectile",
                  ParticleProjectileEntity::new,
                  ParticleProjectileEntityRenderer::new,
                  0.5F, 0.5F, 4, 20);
         public static final EntityType<AmethystShardEntity> AMETHYST_SHARD = register.<AmethystShardEntity>Entity(
                 "amethyst_shard",
                 AmethystShardEntity::new,
                 AmethystShardEntityRenderer::new,
                 0.5f, 0.5f, 4, 20);

          private static void init(){}
     }
     public static class Particles {
          public static final SimpleParticleType SCRATCH = register.Particle("scratch", true, SweepAttackParticle.Factory::new);
          public static final SimpleParticleType GOLDEN_SHIMMER = register.Particle("golden_shimmer", false, FlameParticle.Factory::new);
          public static final SimpleParticleType CURSED_BLOOD = register.Particle("cursed_blood", false, FlameParticle.Factory::new);
          public static final SimpleParticleType EXPLOSION = register.Particle("explosion", false, ExplosionLargeParticle.Factory::new);

          private static void init(){}
     }
     public static void init() {
          Entities.init();
          Particles.init();
     }
}
