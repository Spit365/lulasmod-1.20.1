package net.spit365.lulasmod.mod;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SmokeBombEntity;
import java.util.Objects;
import java.util.Set;
import net.minecraft.entity.damage.DamageTypes;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;

public class ModEvents {
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
                        Vec3d playerVec = player.getPos();
                        Vec3d repelVec = playerVec.subtract(Vec3d.ofCenter(closestPortal)).normalize();

                        if (!repelVec.equals(Vec3d.ZERO)) {
                            player.setVelocity(repelVec);
                            player.velocityModified = true;
                        }
                    }
                }
            }
        });



        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient) {
                if (player.getStackInHand(hand).getItem() == ModItems.MODIFIED_TNT && !player.getItemCooldownManager().isCoolingDown(ModItems.MODIFIED_TNT)) {
                    player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), 7);
                    BlockPos blockPos = BlockPos.ofFloored(player.raycast(1000, 1, false).getPos());
                    world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 2.0f, 1.0f);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.PORTAL, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 200, 0.45d, 0.45d, 0.45d, 1);
                        world.createExplosion(player, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 4.0f, World.ExplosionSourceType.TNT);
                        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 2.0f, 1.0f);
                        ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 50, 0.3d, 0.3d, 0.3d, 1);
                    if (!player.isCreative()) {
                        player.getStackInHand(hand).decrement(1);
                    }
                }

                if (player.getStackInHand(hand).getItem() == ModItems.DRAGON_FIREBALL) {
                    DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, player, player.getRotationVec(1f).getX(), player.getRotationVec(1f).getY(), player.getRotationVec(1f).getZ());
                    dragonFireballEntity.setPosition(player.getX(), player.getY() + 1, player.getZ());
                    world.spawnEntity(dragonFireballEntity);
                    world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS);
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }

                if (player.getStackInHand(hand).getItem() == ModItems.SMOKE_BOMB) {
                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                    SmokeBombEntity smokeBombEntity = new SmokeBombEntity(world, player);
                    smokeBombEntity.setPos(player.getX(), player.getY() + 1, player.getZ());
                    smokeBombEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 0.0F);
                    world.spawnEntity(smokeBombEntity);

                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 400, 0, false, true));
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }

                if (player.getStackInHand(hand).getItem() == ModItems.HOME_BUTTON && !player.getItemCooldownManager().isCoolingDown(ModItems.HOME_BUTTON)){
                    player.getItemCooldownManager().set(ModItems.HOME_BUTTON, 6000);
                    BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
                    if (pos == null){pos = world.getSpawnPos();}
                    player.teleport( pos.getX(), pos.getY() + 1, pos.getZ(), true);
                    Lulasmod.LOGGER.info(player.getName() + " was sent home to " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " (with button)");
                }


                if (player.getOffHandStack().getItem() == ModItems.HELLISH_SEAL && !player.getItemCooldownManager().isCoolingDown(ModItems.HELLISH_SEAL)){
                    if (player.getMainHandStack().getItem() == ModItems.LIGHTNING_CRYSTAL_INCANTATION) {
                        player.getItemCooldownManager().set(ModItems.HELLISH_SEAL, 45);
                        Vec3d pos2 = player.raycast(1000, 1, false).getPos();
                        Entity lightningEntity = new Entity(EntityType.LIGHTNING_BOLT, world) {
                            @Override protected void initDataTracker() {}
                            @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
                            @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}
                        };
                        world.spawnEntity(lightningEntity);
                        lightningEntity.setPosition(pos2);
                        world.createExplosion(lightningEntity,pos2.getX(), pos2.getY(), pos2.getZ(), 5, World.ExplosionSourceType.NONE);
                        player.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC)),20f);
                        world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 100.0f, 10.0f);
                        world.playSound(null, BlockPos.ofFloored(pos2), SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.PLAYERS, 100.0f, 10.0f);
                        ((ServerWorld)world).spawnParticles(ParticleTypes.END_ROD, pos2.getX(), pos2.getY(),pos2.getZ(), 300, 0.3d , 0.3d, 0.3d, 1);
                    }
                    if (player.getMainHandStack().getItem() == ModItems.HOME_INCANTATION){
                        player.getItemCooldownManager().set(ModItems.HELLISH_SEAL, 600);
                        BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
                        if (pos == null){pos = world.getSpawnPos();}
                        player.teleport( pos.getX(), pos.getY() + 1, pos.getZ(), true);
                        Lulasmod.LOGGER.info(player.getName() + " was sent home to " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " (with incantation)");
                    }
                    if (player.getMainHandStack().getItem() == ModItems.SMOKE_INCANTATION){
                        player.getItemCooldownManager().set(ModItems.HELLISH_SEAL, 5);
                        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS);
                        ModImportant.summonSmoke(player.raycast(1000, 1, false).getPos(), world);
                    }
                    if (player.getMainHandStack().getItem() == ModItems.HIGHLIGHTER_INCANTATION) {
                        boolean isPlayerGlowing = !player.isGlowing();
                        world.playSound(null, player.getBlockPos(), (isPlayerGlowing ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
                        for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(isPlayerGlowing);}
                    }
                    if (player.getMainHandStack().getItem() == ModItems.POCKET_INCANTATION){
                        if (world.getRegistryKey().toString().equals("ResourceKey[minecraft:dimension / lulasmod:pocket_dimension]")) {
                            player.teleport(player.getServer().getWorld(World.OVERWORLD), player.getX(), player.getY(), player.getZ(), Set.of() , player.getYaw(), player.getPitch());
                        }else{
                            RegistryKey<World> worldRegistryKey = null;
                            for (RegistryKey<World> worldKeys : Objects.requireNonNull(player.getServer()).getWorldRegistryKeys()){if (
                                worldKeys.toString().equals("ResourceKey[minecraft:dimension / lulasmod:pocket_dimension]")) worldRegistryKey = worldKeys;
                            }
                            if (worldRegistryKey == null) Lulasmod.LOGGER.error("could not find registry key for 'lulasmod:pocket_dimension'");
                            else player.teleport(player.getServer().getWorld(worldRegistryKey), player.getX(), player.getY(), player.getZ(), Set.of() , player.getYaw(), player.getPitch());
                        }
                        player.getItemCooldownManager().set(ModItems.HELLISH_SEAL, 20);
                    }
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }
}
