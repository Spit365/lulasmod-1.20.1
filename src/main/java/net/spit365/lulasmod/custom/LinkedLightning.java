package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.packet.LightningLinkS2CPacket;
import net.spit365.lulasmod.state.LinkedLightningPersistentState;
import net.spit365.lulasmod.util.MultiVec3d;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class LinkedLightning {
    private static final int CURRENT_UPDATE_RANGE = 1000;

    public static void tick(ServerLevel serverWorld) {
        LinkedLightningPersistentState linkedLightningPersistentState = LinkedLightningPersistentState.get(serverWorld);
        Set<MultiVec3d> links = linkedLightningPersistentState.getLinks();
        for (MultiVec3d multiVec3d : links) {
            if (multiVec3d.pairwiseSegments().allMatch(twoVec3d -> twoVec3d.start().distanceTo(twoVec3d.end()) < 0.5d)) {
                linkedLightningPersistentState.remove(multiVec3d);
                break;
            }
            if (handleDamage(serverWorld, multiVec3d, links)) break;
        }
        sendRenderPacket(serverWorld, links);
    }

    private static void sendRenderPacket(ServerLevel serverWorld, Set<MultiVec3d> links) {
        serverWorld.players().forEach(serverPlayer ->
            ServerPlayNetworking.send(serverPlayer, new LightningLinkS2CPacket(
                links.stream().filter(multiVec3d ->
                    Arrays.stream(multiVec3d.vec3ds()).anyMatch(vec3d -> serverPlayer.position().closerThan(vec3d, CURRENT_UPDATE_RANGE))
                ).collect(Collectors.toSet())
            ))
        );
    }

    private static boolean handleDamage(ServerLevel serverWorld, MultiVec3d multiVec3d, Set<MultiVec3d> links) {
        Vec3[] laser = multiVec3d.stream().toArray(Vec3[]::new);
        for (int i = 1; i < laser.length; i++) {
            List<Entity> otherEntities = serverWorld.getEntities(null, new AABB(laser[i - 1], laser[i]));
            if (!otherEntities.isEmpty()) {
                otherEntities.forEach(entity -> {
                    serverWorld.playSound(entity, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS);
                    entity.hurtServer(serverWorld, serverWorld.damageSources().lightningBolt(), 15);
                });
                links.remove(multiVec3d);
                Set<LivingEntity> livingEntitySet = new HashSet<>();
                LinkedLightningPersistentState.lastLinks.entrySet().removeIf(entityMultiVec3dEntry -> {
                    boolean result = entityMultiVec3dEntry.getValue().equals(multiVec3d);
                    if (result && entityMultiVec3dEntry.getKey() instanceof LivingEntity livingEntity) livingEntitySet.add(livingEntity);
                    return result;
                });
                livingEntitySet.forEach(LivingEntity::releaseUsingItem);
                return true;
            }
        }
        return false;
    }

}
