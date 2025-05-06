package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.item.SpellItem;
import net.spit365.lulasmod.tag.TagManager;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static net.minecraft.sound.SoundEvents.*;
import static net.spit365.lulasmod.mod.ModItems.CreativeTabItems;

public class ModSpells {
     public static final Item BLOOD_FLAME_SPELL = register("treachery_judecca", new SpellItem() {
          @Override protected SoundEvent sound() {return ENTITY_ZOMBIE_VILLAGER_CURE;}
          @Override public int cooldown() {return 3;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
               for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))) {
                    if (entity instanceof LivingEntity livingEntity) ModMethods.applyBleed(livingEntity, (int) (120 * efficiencyMultiplier));
                    else entity.discard();}
               world.spawnParticles(ModParticles.BLOOD_FLAME, pos.getX(), pos.getY(), pos.getZ(), 37, 0.5, 0.5, 0.5, 0);
               world.playSound(null, player.getBlockPos(), ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 100.0f, 10f);
          }
     },false);
     public static final Item FIRE_SPELL = register("malignity", new SpellItem() {
          @Override public int cooldown() {return 300;}
          @Override protected SoundEvent sound() {return BLOCK_RESPAWN_ANCHOR_CHARGE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(efficiencyMultiplier +2), 100)));
          }
     },false);
     public static final Item DASH_SPELL = register("purloining", new SpellItem() {
          @Override public int cooldown() {return 0;}
          @Override protected SoundEvent sound() {return BLOCK_RESPAWN_ANCHOR_CHARGE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               if (!player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                    Identifier id = TagManager.read(player, ModTagCategories.DASH_SPELL);
                    if (id == null){
                         id = new Identifier(Lulasmod.MOD_ID, String.valueOf(5 * cooldownMultiplier));
                         TagManager.put(player, ModTagCategories.DASH_SPELL, id);
                    }
                    String usages = id.getPath();
                    player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), (usages.equals("1")? 50 : 2));
                    TagManager.put(player, ModTagCategories.DASH_SPELL, new Identifier(Lulasmod.MOD_ID, String.valueOf(
                            (usages.equals("1")?
                                    5 * cooldownMultiplier :
                                    Math.min(5 * cooldownMultiplier, Integer.parseInt(usages)) - 1)
                    )));
                    player.addVelocity(player.getRotationVec(1).normalize().add(0, 0.25, 0));
                    player.velocityModified = true;
                    world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
               } else player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), 20);
          }
     },false);
     public static final Item SMOKE_SPELL = register("guile", new SpellItem() {
          @Override public int cooldown() {return 20;}
          @Override protected SoundEvent sound() {return BLOCK_RESPAWN_ANCHOR_CHARGE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               world.playSound(null, player.getBlockPos(), ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);
               world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player.getPos().x, player.getPos().y + 1.0d, player.getPos().z, 269, 1.2d, 1.2d, 1.2d, 0d);
               player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 600, 0, false, false));
               if (efficiencyMultiplier > 1) player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.CUSHIONED, Math.round(efficiencyMultiplier) *1200, 0, false, false));
          }
     },false);
     public static final Item HEAL_SPELL = register("appeasing", new SpellItem() {
          @Override public int cooldown() {return 300;}
          @Override protected SoundEvent sound() {return BLOCK_RESPAWN_ANCHOR_CHARGE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               player.setHealth(player.getMaxHealth());
          }
     },false);
     public static final Item BLOOD_SPELL = register("emulations", new SpellItem() {
          @Override public int cooldown() {return 0;}
          @Override protected SoundEvent sound() {return ENTITY_ZOMBIE_VILLAGER_CURE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity victim)
                    ModMethods.applyBleed(victim, (int) (1200 * efficiencyMultiplier) -80);
               ModMethods.impale(player, player.getStackInHand(hand).getItem(), 20, 600, 6, ModParticles.CURSED_BLOOD);
          }
     },false);
     public static final Item HOME_SPELL = register("wickedness", new SpellItem() {
          @Override public int cooldown() {return 600;}
          @Override protected SoundEvent sound() {return BLOCK_RESPAWN_ANCHOR_CHARGE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               ModMethods.sendHome(player, player.getStackInHand(hand).getItem());
          }
     },false);
     public static final Item POCKET_SPELL = register("heresies", new SpellItem() {
          @Override public int cooldown() {return 300;}
          @Override protected SoundEvent sound() {return ENTITY_ZOMBIE_VILLAGER_CURE;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               List<Entity> entities = world.getOtherEntities(player, new Box(player.getPos().add(-5d, -5d, -5d), player.getPos().add(5d, 5d, 5d)));
               if (entities.isEmpty()) entities.add(player);
               for (Entity victim : entities) {
                    world.spawnParticles(ParticleTypes.PORTAL, victim.getX(), victim.getY() + 0.5, victim.getZ(), 100, 0, 0, 0, 1);
                    if (!victim.teleport(Objects.requireNonNull(victim.getServer()).getWorld((
                            world.getRegistryKey().equals(ModDimensions.POCKET_DIMENSION)?
                                    World.OVERWORLD :
                                    ModDimensions.POCKET_DIMENSION
                    )), victim.getX(), victim.getY(), victim.getZ(), Set.of() , victim.getYaw(), victim.getPitch()))
                         Lulasmod.LOGGER.error("Could not perform teleport. Registry key: {}, Entity: {}", ModDimensions.POCKET_DIMENSION, victim);
               }
          }
     },false);
     public static final Item HIGHLIGHTER_SPELL = register("highlighter_spell", new SpellItem() {
          @Override public int cooldown() {return 0;}
          @Override protected SoundEvent sound() {return null;}
          @Override public void execute(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               boolean playerGlowing = !player.isGlowing();
               world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
               for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(playerGlowing);}
          }
     },true);

     private static Item register(String name, Item item, Boolean showInCreativeTab) {
          if (showInCreativeTab) CreativeTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
          return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
     }
     
     public static void init() {}
}