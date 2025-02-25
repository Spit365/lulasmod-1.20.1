package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SmokeBombEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Timer;
import java.util.TimerTask;

public class ModEvents {
    public static void init(){
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient) {
                if (player.getStackInHand(hand).getItem() == ModItems.MODIFIED_TNT) {
                    BlockPos blockPos = BlockPos.ofFloored(player.raycast(99999, 1, false).getPos());
                    world.playSound(null, player.getBlockPos(),SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE,SoundCategory.PLAYERS,2.0f,1.0f);
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            timer.cancel();
                            world.createExplosion(player, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 5, World.ExplosionSourceType.TNT);
                            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS,2.0f, 1.0f);
                        }
                    };
                    timer.schedule(task, 1500);
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }

                if (player.getStackInHand(hand).getItem() == ModItems.DRAGON_FIREBALL) {
                    DragonFireballEntity dragonFireballEntity = new DragonFireballEntity(world, player, player.getRotationVec(1f).getX(), player.getRotationVec(1f).getY(), player.getRotationVec(1f).getZ());
                    dragonFireballEntity.setPosition(player.getX(), player.getY() + 1, player.getZ());
                    world.spawnEntity(dragonFireballEntity);
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }

                if (player.getStackInHand(hand).getItem() == ModItems.HIGHLIGHTER) {
                    boolean iPlayerGlowing = !player.isGlowing();
                    for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(iPlayerGlowing);}
                }

                if (player.getStackInHand(hand).getItem() == ModItems.LIGHTNING_CRYSTAL) {
                    Vec3d pos2 = player.raycast(100, 1, false).getPos();
                    Entity lightningEntity = new Entity(EntityType.LIGHTNING_BOLT, world) {
                        @Override protected void initDataTracker() {}
                        @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
                        @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}
                    };
                    world.spawnEntity(lightningEntity);
                    lightningEntity.setPosition(pos2);
                    world.createExplosion(lightningEntity,pos2.getX(), pos2.getY(), pos2.getZ(), 5, World.ExplosionSourceType.NONE);
                    player.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC)),(player.getCommandTags().stream().anyMatch("tailed"::equals) ? 0f : 4f));
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

                if (player.getStackInHand(hand).getItem() == ModItems.HOME_BUTTON){
                    BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
                    if (pos == null){pos = world.getSpawnPos();}
                    player.teleport( pos.getX(), pos.getY(), pos.getZ(), true);
                    Lulasmod.LOGGER.info(player.getName() + " homebuttoned to " + pos.getX() + pos.getY() + pos.getZ());
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }
}
