package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.*;
import net.spit365.lulasmod.custom.entity.renderer.PaperEntityRenderer;
import net.spit365.lulasmod.custom.entity.renderer.SmokeCreeperEntityRenderer;

public class ModEntities {
    public static final EntityType<SmokeBombEntity> SMOKE_BOMB = register(
        "smoke_bomb",
        EntityType.Builder.<SmokeBombEntity>create(SmokeBombEntity::new, SpawnGroup.MISC)
                .setDimensions(0.25F, 0.25F)
                .maxTrackingRange(4)
                .trackingTickInterval(10)
    );
    public static final EntityType<SmokeCreeperEntity> SMOKE_CREEPER = register(
        "smoke_creeper",
        EntityType.Builder.create(SmokeCreeperEntity::new, SpawnGroup.MONSTER)
                .setDimensions(0.6F, 1.7F)
    );
    public static final EntityType<PaperEntity> PAPER = register(
            "paper",
            EntityType.Builder.create(PaperEntity::new,
                            SpawnGroup.MISC)
                    .setDimensions(0.5F, 0.5F)
    );

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(Lulasmod.MOD_ID, id), type.build(new Identifier(Lulasmod.MOD_ID, id).toString()));
    }
    public static void init(){
       EntityRendererRegistry.register(ModEntities.SMOKE_BOMB, FlyingItemEntityRenderer::new);
       EntityRendererRegistry.register(ModEntities.SMOKE_CREEPER, SmokeCreeperEntityRenderer::new);
       EntityRendererRegistry.register(ModEntities.PAPER, PaperEntityRenderer::new);

       FabricDefaultAttributeRegistry.register(SMOKE_CREEPER, SmokeCreeperEntity.createMobAttributes());
    }
}