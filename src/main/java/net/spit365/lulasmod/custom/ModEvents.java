package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Timer;
import java.util.TimerTask;

import static net.spit365.lulasmod.Lulasmod.creeperExplode;
import static net.spit365.lulasmod.Lulasmod.whoExplode;

public class ModEvents {
    public static void init(){
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient) {
                if (player.getStackInHand(hand).getItem() == ModItems.MODIFIED_TNT) {
                    player.setVelocity(0, 1, 0);
                    BlockPos pos1 = BlockPos.ofFloored(player.raycast(999999, 1, false).getPos());
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            world.createExplosion(player, pos1.getX(), pos1.getY(), pos1.getZ(), 5, World.ExplosionSourceType.TNT);
                            timer.cancel();
                        }
                    };
                    timer.schedule(task, 3000);
                }
                if (player.getStackInHand(hand).getItem() == ModItems.DRAGON_FIREBALL) {
                    DragonFireballEntity fireballEntity = new DragonFireballEntity(world, player, player.getRotationVec(1f).getX(), player.getRotationVec(1f).getY(), player.getRotationVec(1f).getZ());
                    fireballEntity.setPosition(player.getX(), player.getY() + 1, player.getZ());
                    world.spawnEntity(fireballEntity);
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }
                if (player.getStackInHand(hand).getItem() == ModItems.HIGHLIGHTER) {
                    boolean iPlayerGlowing = !player.isGlowing();
                    for (int i = 0; i <= world.getPlayers().size(); i++){world.getPlayers().get(i).setGlowing(iPlayerGlowing);}
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }
                if (player.getStackInHand(hand).getItem() == ModItems.LIGHTNING_CRYSTAL) {
                    BlockPos pos2 = BlockPos.ofFloored(player.raycast(100, 1, false).getPos());
                    Entity lightningEntity = new Entity(EntityType.LIGHTNING_BOLT, world) {
                        @Override protected void initDataTracker() {}
                        @Override protected void readCustomDataFromNbt(NbtCompound nbt) {}
                        @Override protected void writeCustomDataToNbt(NbtCompound nbt) {}
                    };
                    world.spawnEntity(lightningEntity);
                    lightningEntity.setPosition(new Vec3d(pos2.getX(), pos2.getY(), pos2.getZ()));
                    world.createExplosion(lightningEntity,pos2.getX(), pos2.getY(), pos2.getZ(), 5, World.ExplosionSourceType.NONE);
                    player.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC)), 4f);
                }
                if (player.getStackInHand(hand).getItem() == ModItems.GRAVITATOR) {
                    player.setNoGravity(!player.hasNoGravity());
                }
                if (player.getStackInHand(hand).getItem() == ModItems.SMOKE_BOMB){
                    world.playSound(null,	player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                    SmokeBombEntity smokeBombEntity = new SmokeBombEntity(world, player);
                    smokeBombEntity.setPos(player.getX(), player.getY() + 1, player.getZ());
                    smokeBombEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, 1.5F, 0.0F);
                    world.spawnEntity(smokeBombEntity);

                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 400, 0, false, true));
                    if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
                }
                if (player.getStackInHand(hand).getItem() == ModItems.DETONATOR){
                    creeperExplode = !creeperExplode;
                    player.sendMessage(Text.of(whoExplode + (creeperExplode ? " now dies from creepers" : " is now safe from creepers")));
                }
            }
            return TypedActionResult.pass(player.getStackInHand(hand));
        });
    }
}
