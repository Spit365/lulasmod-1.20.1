package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.tag.TagManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ModMethods {
    private static final LinkedList<ImpaledContext> impaled = new LinkedList<>();
    private static int impaledCounter = 0;
    private record ImpaledContext(PlayerEntity player, LivingEntity livingEntity, ParticleEffect particle, Integer iterations) {
        public ImpaledContext(ImpaledContext context, Integer iterations) {
            this(context.player(), context.livingEntity(), context.particle(), iterations);
        }
    }

    public static @Nullable Entity selectClosestEntity(Entity selector, Double radius) {
        Vec3d selectionCenter = selector.getRotationVec(1).normalize().multiply(radius).add(selector.getPos());
        Entity selectedEntity = null;
        for (Entity entityInRange : selector.getWorld().getOtherEntities(selector, new Box(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))){
            if (selectedEntity == null || selectedEntity.getPos().squaredDistanceTo(selector.getPos()) > entityInRange.getPos().squaredDistanceTo(selector.getPos())){
                selectedEntity = entityInRange;
            }
        }
        return selectedEntity;
    }

    public static void applyBleed(LivingEntity entity, Integer duration){
        StatusEffectInstance effectInstance = entity.getStatusEffect(ModStatusEffects.BLEEDING);
        if (effectInstance != null){
            entity.setStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, effectInstance.getDuration() + duration), entity);
        } else entity.addStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, duration));
    }

    public static Boolean impale(PlayerEntity player, Item item, Integer baseCooldown, Integer maxCooldown, Integer iterations, ParticleEffect particle) {
        player.getItemCooldownManager().set(item, 2);
        if (selectClosestEntity(player, 5d) instanceof LivingEntity selectedEntity) {
            selectedEntity.requestTeleport(selectedEntity.getX(), selectedEntity.getY() + 5, selectedEntity.getZ());
            player.getItemCooldownManager().set(item, maxCooldown);
            impaled.add(new ImpaledContext(player, selectedEntity, particle, iterations));
            return true;
        } else player.getItemCooldownManager().set(item, baseCooldown - 2);
        return false;
    }

    protected static class ServerUpdates {
        protected static void updateImpaled() {
            impaledCounter++;
            for (ImpaledContext context : impaled) {
                LivingEntity victim = context.livingEntity;
                if (context.iterations > 0 && victim.isAlive()) {
                    if (!(victim instanceof EndermanEntity)) {
                        victim.setVelocity(0, 0, 0);
                        if (impaledCounter >= 25) {
                            impaledCounter = 0;
                            Vec3d pos = new Vec3d(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1).normalize().multiply(5).add(victim.getPos());
                            ParticleProjectileEntity projectile = new ParticleProjectileEntity(victim.getWorld(), context.player, context.particle);
                            projectile.setNoGravity(true);
                            projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                            TagManager.put(context.player, ModTagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                            projectile.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                            projectile.addVelocity(pos.subtract(victim.getPos()).multiply(-0.5));
                            victim.getWorld().spawnEntity(projectile);
                            impaled.remove(context);
                            impaled.add(new ImpaledContext(context, context.iterations -1));
                        }
                    } else victim.kill();
                } else {
                    impaled.remove(context);
                    TagManager.remove(context.player, ModTagCategories.DAMAGE_DELAY);
                    victim.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 50));
                }
            }
        }
        protected static void updateSpells(ServerPlayerEntity player) {
            PacketByteBuf buf = PacketByteBufs.create();
            LinkedList<Identifier> list = TagManager.readList(player, ModTagCategories.SPELLS);

            Map<Integer, ItemStack> map = new HashMap<>();
            for (int i = 0; i < list.size(); i++) map.put(i, new ItemStack(Registries.ITEM.get(list.get(i))));
            buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
            ServerPlayNetworking.send(player, ModPackets.PLAYER_SPELL_LIST, buf);
        }
        protected static void repelMiner(ServerPlayerEntity player) {
            if (player.getCommandTags().contains("miner")) {
                BlockPos playerPos = player.getBlockPos();
                int portalRadius = 5;
                BlockPos closestPortal = null;
                double closestDistance = Double.MAX_VALUE;
                for (BlockPos pos : BlockPos.stream(playerPos.add(-portalRadius, -portalRadius, -portalRadius),
                                playerPos.add(portalRadius, portalRadius, portalRadius))
                        .map(BlockPos::toImmutable)
                        .toList()) {
                    if (player.getWorld().getBlockState(pos).isOf(Blocks.END_PORTAL) ||
                            player.getWorld().getBlockState(pos).isOf(Blocks.NETHER_PORTAL)) {
                        double distance = pos.getSquaredDistance(playerPos);
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestPortal = pos;
                        }
                    }
                }
                if (closestPortal != null) {
                    Vec3d repelVec = player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize();
                    if (!repelVec.equals(Vec3d.ZERO)) {
                        player.setVelocity(repelVec);
                        player.velocityModified = true;
                    }
                }
            }
        }
    }
}
