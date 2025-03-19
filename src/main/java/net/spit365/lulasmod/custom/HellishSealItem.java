package net.spit365.lulasmod.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModImportant;
import net.spit365.lulasmod.mod.ModItems;

import java.util.Objects;
import java.util.Set;

public class HellishSealItem extends Item {
    public HellishSealItem(Settings settings) {
        super(settings);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient() &&  !player.getItemCooldownManager().isCoolingDown(this) && player.getCommandTags().contains("tailed")) {
            if (SpellManager.getSpells(player).get(1).getItem() == ModItems.LIGHTNING_CRYSTAL_INCANTATION) {
                player.getItemCooldownManager().set(this, 45);
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
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (SpellManager.getSpells(player).get(1).getItem() == ModItems.HOME_INCANTATION){
                player.getItemCooldownManager().set(this, 600);
                BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
                if (pos == null){pos = world.getSpawnPos();}
                player.teleport( pos.getX(), pos.getY() + 1, pos.getZ(), true);
                Lulasmod.LOGGER.info(player.getName() + " was sent home to " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " (with incantation)");
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (SpellManager.getSpells(player).get(1).getItem() == ModItems.SMOKE_INCANTATION){
                player.getItemCooldownManager().set(this, 5);
                world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS);
                ModImportant.summonSmoke(player.raycast(1000, 1, false).getPos(), world);
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (SpellManager.getSpells(player).get(1).getItem() == ModItems.HIGHLIGHTER_INCANTATION) {
                boolean isPlayerGlowing = !player.isGlowing();
                world.playSound(null, player.getBlockPos(), (isPlayerGlowing ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
                for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(isPlayerGlowing);}
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (SpellManager.getSpells(player).get(1).getItem() == ModItems.POCKET_INCANTATION){
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
                player.getItemCooldownManager().set(this, 20);
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
