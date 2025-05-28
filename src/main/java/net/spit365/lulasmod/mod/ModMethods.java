package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.tag.TagManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ModMethods {
    private static final LinkedList<ImpaledContext> impaled = new LinkedList<>();
    private static int impaledCounter = 0;
    private static int sporesCounter = 0;

    private record ImpaledContext(PlayerEntity player, LivingEntity livingEntity, ParticleEffect particle, Integer iterations) {
        public ImpaledContext(ImpaledContext context, Integer iterations) {
            this(context.player(), context.livingEntity(), context.particle(), iterations);
        }
    }

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
        StatusEffectInstance effectInstance = entity.getStatusEffect(Mod.StatusEffects.BLEEDING);
        if (effectInstance != null){
            entity.setStatusEffect(new StatusEffectInstance(Mod.StatusEffects.BLEEDING, effectInstance.getDuration() + duration), entity);
        } else entity.addStatusEffect(new StatusEffectInstance(Mod.StatusEffects.BLEEDING, duration));
    }

    public static void sendHome(PlayerEntity player, Item item){
        if (player instanceof ServerPlayerEntity serverPlayer) {
            BlockPos pos = serverPlayer.getSpawnPointPosition();
            if (pos == null){
                pos = player.getWorld().getSpawnPos();
            } else player.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
            RegistryKey<World> targetDimension = serverPlayer.getSpawnPointDimension();
            if (player.teleport(Objects.requireNonNull(player.getServer()).getWorld(targetDimension), pos.getX(), pos.getY(), pos.getZ(), Set.of(), player.getYaw(), player.getPitch()))
                Lulasmod.LOGGER.info("{} was sent home to {} {} {} {} (with {})", player.getName(), pos.getX(), pos.getY(), pos.getZ(), targetDimension, item);
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

    public static void outlineBox(Box box, ServerWorld world){
        final Vec3d start = box.getCenter().add(box.getXLength() / -2, box.getYLength() / -2, box.getZLength() / -2);
        final Vec3d end = box.getCenter().add(box.getXLength() / 2, box.getYLength() / 2, box.getZLength() / 2);

        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX() + i, start.getY(), start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX() + i, start.getY(), end.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX() + i, end.getY(), start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getXLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX() + i, end.getY(), end.getZ(), 0, 0, 0, 0, 0);

        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, end.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getYLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, end.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);

        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, start.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, end.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
        for (double i = 0; i < box.getZLength(); i += 0.625) world.spawnParticles(Mod.Particles.GOLDEN_SHIMMER, end.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);

    }

    private static void sendSpellListPacket(ServerPlayerEntity player, LinkedList<Identifier> list) {
        Map<Integer, ItemStack> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++)
            map.put(i, new ItemStack(Registries.ITEM.get(list.get(i))));
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
        ServerPlayNetworking.send(player, Mod.Packets.PLAYER_SPELL_LIST, buf);
    }

    public static Boolean impale(PlayerEntity player, Item item, Integer baseCooldown, Integer maxCooldown, Integer iterations, ParticleEffect particle) {
        player.getItemCooldownManager().set(item, 2);
        if (selectClosestEntity(player, 5d) instanceof LivingEntity selectedEntity) {
            player.getItemCooldownManager().set(item, maxCooldown);
            selectedEntity.requestTeleport(selectedEntity.getX(), selectedEntity.getY() + 5, selectedEntity.getZ());
            impaled.add(new ImpaledContext(player, selectedEntity, particle, iterations));
            return true;
        } else player.getItemCooldownManager().set(item, baseCooldown - 2);
        return false;
    }

    public static class ServerUpdates {
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
                            TagManager.put(context.player, Mod.TagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                            victim.getWorld().spawnEntity(new ParticleProjectileEntity(
                                    victim.getWorld(), context.player, pos, pos.subtract(victim.getPos()).multiply(-0.5), context.particle)
                            );
                            impaled.remove(context);
                            impaled.add(new ImpaledContext(context, context.iterations -1));
                        }
                    } else victim.kill();
                } else {
                    impaled.remove(context);
                    TagManager.remove(context.player, Mod.TagCategories.DAMAGE_DELAY);
                    victim.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SLOW_FALLING, 50));
                }
            }
        }
        protected static void updateSpells(ServerPlayerEntity player) {
            if(player.getMainHandStack().getItem() instanceof SpellHotbar item) sendSpellListPacket(player, item.display(player));
            else if(player.getOffHandStack().getItem() instanceof SpellHotbar item) sendSpellListPacket(player, item.display(player));
        }
        protected static void repelMiner(ServerPlayerEntity player) {
            if (player.getCommandTags().contains("miner")) {
                BlockPos playerPos = player.getBlockPos();
                BlockPos closestPortal = null;
                for (BlockPos pos : BlockPos.stream(
                    playerPos.add(-5, -5, -5),
                    playerPos.add(5, 5, 5)
                    ).map(BlockPos::toImmutable)
                    .toList())
                    if (player.getWorld().getBlockState(pos).isOf(Blocks.END_PORTAL) ||
                        player.getWorld().getBlockState(pos).isOf(Blocks.NETHER_PORTAL))
                        if (closestPortal == null || pos.getSquaredDistance(playerPos) < closestPortal.getSquaredDistance(playerPos))
                            closestPortal = pos;
                if (closestPortal != null) {
                    Vec3d repelVec = player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize();
                    if (!repelVec.equals(Vec3d.ZERO)) {
                        player.setVelocity(repelVec);
                        player.velocityModified = true;
                    }
                }
            }
        }
        protected static void updateTailedVisuals(MinecraftServer minecraftServer){
            sporesCounter--;
            Map<Integer, String> tailedPlayers = new HashMap<>();
            minecraftServer.getPlayerManager().getPlayerList().forEach(player -> {
                if (player.getCommandTags().contains("tailed")) {
                    tailedPlayers.put(tailedPlayers.size(), player.getUuidAsString());
                    if (sporesCounter <= 0 && player.getWorld() instanceof ServerWorld world){
                        world.spawnParticles(ParticleTypes.CRIMSON_SPORE, player.getX(), player.getY() +1, player.getZ(), player.getRandom().nextBetweenExclusive(2, 4), 0, 0, 0, 0);
            }}});
            if (sporesCounter <= 0) sporesCounter = new Random().nextInt(30, 60);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeMap(tailedPlayers, PacketByteBuf::writeInt, PacketByteBuf::writeString);
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList())
                ServerPlayNetworking.send(player, Mod.Packets.TAILED_PLAYER_LIST, buf);
        }
    }
}