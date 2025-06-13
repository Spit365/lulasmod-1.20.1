package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Server;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModMethods {
    public static final LinkedList<ModServer.Tickers.ImpaledContext> impaled = new LinkedList<>();

    public static @Nullable Entity selectClosestEntity(Entity selector, Double radius) {
        Vec3d selectionCenter = selector.getRotationVec(1).normalize().multiply(radius).add(selector.getPos());
        Entity selectedEntity = null;
        for (Entity entityInRange : selector.getWorld().getOtherEntities(selector, new Box(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))){
            if (selectedEntity == null || selectedEntity.getPos().squaredDistanceTo(selector.getPos()) > entityInRange.getPos().squaredDistanceTo(selector.getPos()))
                selectedEntity = entityInRange;
        }
        return selectedEntity;
    }

    public static void applyBleed(LivingEntity entity, Integer duration){
        StatusEffectInstance effectInstance = entity.getStatusEffect(ModServer.StatusEffects.BLEEDING);
        if (effectInstance != null){
            entity.setStatusEffect(new StatusEffectInstance(ModServer.StatusEffects.BLEEDING, effectInstance.getDuration() + duration), entity);
        } else entity.addStatusEffect(new StatusEffectInstance(ModServer.StatusEffects.BLEEDING, duration));
    }

    public static void sendHome(PlayerEntity player, Item item){
        if (player instanceof ServerPlayerEntity serverPlayer) {
            BlockPos pos = serverPlayer.getSpawnPointPosition();
            if (pos == null){
                pos = player.getWorld().getSpawnPos();
            } else player.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
            RegistryKey<World> targetDimension = serverPlayer.getSpawnPointDimension();
            if (player.teleport(Objects.requireNonNull(player.getServer()).getWorld(targetDimension), pos.getX(), pos.getY(), pos.getZ(), Set.of(), player.getYaw(), player.getPitch()))
                Server.LOGGER.info("{} was sent home to {} {} {} {} (with {})", player.getName(), pos.getX(), pos.getY(), pos.getZ(), targetDimension, item);
        }
    }

    public static ItemStack getItemStack(PlayerEntity player, Item item){
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.getItem().equals(item)) {
                return itemStack;
            }
        }
        return null;
    }

    public static void outlineBox(Box box, ServerWorld world, DefaultParticleType particle){
        final Vec3d start = box.getCenter().add(box.getXLength() / -2, box.getYLength() / -2, box.getZLength() / -2);
        final Vec3d end = box.getCenter().add(box.getXLength() / 2, box.getYLength() / 2, box.getZLength() / 2);

        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(particle, start.getX() + i, start.getY(), start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(particle, start.getX() + i, start.getY(), end.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(particle, start.getX() + i, end.getY(), start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(particle, start.getX() + i, end.getY(), end.getZ(), 0, 0, 0, 0, 0);

        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);

        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(particle, start.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(particle, end.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
    }

    public static Boolean impale(PlayerEntity player, Item item, Integer baseCooldown, Integer maxCooldown, Integer iterations, ParticleEffect particle) {
        player.getItemCooldownManager().set(item, 2);
        if (selectClosestEntity(player, 5d) instanceof LivingEntity selectedEntity) {
            player.getItemCooldownManager().set(item, maxCooldown);
            selectedEntity.requestTeleport(selectedEntity.getX(), selectedEntity.getY() + 5, selectedEntity.getZ());
            impaled.add(new ModServer.Tickers.ImpaledContext(player, selectedEntity, particle, iterations));
            return true;
        } else player.getItemCooldownManager().set(item, baseCooldown - 2);
        return false;
    }

    public static void pocketTeleport(Entity victim) {
        if (!victim.teleport(Objects.requireNonNull(victim.getServer()).getWorld((
                victim.getWorld().getRegistryKey().equals(ModServer.Dimensions.POCKET_DIMENSION)?
                        World.OVERWORLD :
                        ModServer.Dimensions.POCKET_DIMENSION
        )), victim.getX(), victim.getY(), victim.getZ(), EnumSet.noneOf(PositionFlag.class), victim.getYaw(), victim.getPitch()))
            Server.LOGGER.error("Could not perform teleport. Registry key: {}, Entity: {}", ModServer.Dimensions.POCKET_DIMENSION, victim);
    }
}