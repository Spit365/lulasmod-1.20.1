package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.item.seal.CatalystItem;
import net.spit365.lulasmod.tag.TagManager;
import java.util.*;

public class ModServerEvents {

    public static final List<ParticleProjectileEntity> particle_projectiles = new LinkedList<>();

    @SuppressWarnings("deprecation")
    public static void init(){
        ServerTickCallback.EVENT.register(minecraftServer -> {
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                if (player.getCommandTags().contains("miner")) {
                    BlockPos playerPos = player.getBlockPos();
                    int portalRadius = 5;
                    BlockPos closestPortal = null;
                    double closestDistance = Double.MAX_VALUE;
                    for (BlockPos pos : BlockPos.stream(playerPos.add(-portalRadius, -portalRadius, -portalRadius),
                            playerPos.add(portalRadius, portalRadius, portalRadius))
                            .map(BlockPos::toImmutable)
                            .toList()) {
                        if (player.getWorld().getBlockState(pos).isOf(Blocks.END_PORTAL)||
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
            for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()){
                Identifier read = TagManager.read(player, ModTagCategories.VICTIM);
                if (read != null){
                    Entity victim = player.getWorld().getEntityById(Integer.parseInt(read.getPath()));
                    if (victim != null && victim.isAlive()) {
                        victim.setVelocity(0, 0, 0);
                        Random random = new Random();
                        Vec3d pos = new Vec3d(Math.random() * (random.nextBoolean()? 1 : -1), Math.random() * (random.nextBoolean()? 1 : -1), Math.random() * (random.nextBoolean()? 1 : -1)).normalize().multiply(5).add(victim.getPos());
                        if (!(victim instanceof EndermanEntity)) {
                            ParticleProjectileEntity projectile = new ParticleProjectileEntity(victim.getWorld(), player, (ParticleEffect) Registries.PARTICLE_TYPE.get(new Identifier(Lulasmod.MOD_ID, read.getNamespace())));
                            particle_projectiles.add(projectile);
                            projectile.setNoGravity(true);
                            projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                            TagManager.put(projectile, ModTagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                            projectile.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                            projectile.addVelocity(pos.subtract(victim.getPos()).multiply(-0.5));
                            victim.getWorld().spawnEntity(projectile);
                        }else victim.kill();
                    } else {for (ParticleProjectileEntity p : particle_projectiles) p.kill(); particle_projectiles.clear(); TagManager.remove(player, ModTagCategories.VICTIM);}
                }
            }
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()){
                PacketByteBuf buf = PacketByteBufs.create();
                LinkedList<Identifier> list = TagManager.readList(player, ModTagCategories.SPELLS);

                Map<Integer, ItemStack> map = new HashMap<>();
                for (int i = 0; i < list.size(); i++) map.put(i, new ItemStack(Registries.ITEM.get(list.get(i))));
                buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
                ServerPlayNetworking.send(player, ModPackets.PLAYER_SPELL_LIST, buf);
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.CYCLE_PLAYER_SPELL, (a, player, b, c, d) -> {
            if (
                player.getMainHandStack().getItem() instanceof CatalystItem ||
                player.getOffHandStack().getItem() instanceof CatalystItem
            ) TagManager.cycle(player, ModTagCategories.SPELLS);
        });
    }
}
