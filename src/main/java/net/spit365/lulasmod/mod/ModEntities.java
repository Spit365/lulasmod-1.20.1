package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.*;
import net.spit365.lulasmod.custom.entity.renderer.GoldenProjectileEntityRenderer;

public class ModEntities {
    public static final EntityType<SmokeBombEntity> SMOKE_BOMB = register(
        "smoke_bomb",
        EntityType.Builder.<SmokeBombEntity>create(SmokeBombEntity::new, SpawnGroup.MISC)
                .setDimensions(0.25F, 0.25F)
                .maxTrackingRange(4)
                .trackingTickInterval(10)
    );public static final EntityType<MalignityEntity> MALIGNITY = register(
        "malignity",
        EntityType.Builder.<MalignityEntity>create(MalignityEntity::new, SpawnGroup.MISC)
                .setDimensions(1.0F, 1.0F)
                .maxTrackingRange(4)
                .trackingTickInterval(10)
    );public static final EntityType<GoldenProjectileEntity> GOLDEN_PROJECTILE = register(
        "golden_projectile",
        EntityType.Builder.<GoldenProjectileEntity>create(GoldenProjectileEntity::new, SpawnGroup.MISC)
                .setDimensions(0.5F, 0.5F)
                .maxTrackingRange(4)
                .trackingTickInterval(20)
    );

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(Lulasmod.MOD_ID, id), type.build(new Identifier(Lulasmod.MOD_ID, id).toString()));
    }
    public static void init(){
       EntityRendererRegistry.register(ModEntities.SMOKE_BOMB, FlyingItemEntityRenderer::new);
       EntityRendererRegistry.register(ModEntities.MALIGNITY, FlyingItemEntityRenderer::new);
       EntityRendererRegistry.register(ModEntities.GOLDEN_PROJECTILE, GoldenProjectileEntityRenderer::new);
    }
}