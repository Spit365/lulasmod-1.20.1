package net.spit365.lulasmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.spit365.lulasmod.custom.SmokeBombEntity;

public class ModEntities {
    public static final EntityType<SmokeBombEntity> SMOKE_BOMB = register(
            "smoke_bomb",
            EntityType.Builder.<SmokeBombEntity>create(SmokeBombEntity::new, SpawnGroup.MISC).setDimensions(0.25F, 0.25F).maxTrackingRange(4).trackingTickInterval(10)
    );
    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, id, type.build(id));
    }
}
