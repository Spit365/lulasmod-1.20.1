package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.effect.BleedingStatusEffect;
import net.spit365.lulasmod.custom.effect.CushionedStatusEffect;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.entity.SmokeBombEntity;
import net.spit365.lulasmod.custom.entity.renderer.ParticleProjectileEntityRenderer;
import net.spit365.lulasmod.custom.item.*;
import net.spit365.lulasmod.custom.item.seal.BloodsuckingSeal;
import net.spit365.lulasmod.custom.item.seal.GoldenSeal;
import net.spit365.lulasmod.custom.item.seal.HellishSeal;
import net.spit365.lulasmod.tag.TagManager;

import java.util.*;

import static net.minecraft.sound.SoundEvents.*;

public class Mod {
     private static GameRules.Key<GameRules.BooleanRule> registerGameRule(String name, GameRules.Category category, boolean defaultValue) {
          return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
     }
     private static RegistryKey<DamageType> registerDamageType(String name){
          return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Lulasmod.MOD_ID, name));
     }
     private static StatusEffect registerStatusEffect(String id, StatusEffect entry) {
          return Registry.register(Registries.STATUS_EFFECT, new Identifier(Lulasmod.MOD_ID, id), entry);
     }
     private static ItemGroup registerItemGroup(String name, Item item, List<Identifier> list) {
          return Registry.register(Registries.ITEM_GROUP, new Identifier(Lulasmod.MOD_ID, name), FabricItemGroup.builder()
                  .displayName(Text.translatable("item_group." + name))
                  .icon(() -> new ItemStack(item))
                  .entries((displayContext, entries) -> {for (Identifier id : list) entries.add(Registries.ITEM.get(id));})
                  .build());
     }
     private static <T extends Entity> EntityType<T> registerEntity(String id, EntityType.EntityFactory<T> entityFactory, EntityRendererFactory<T> entityRendererFactory, float width, float height, int maxTrackingRange, int trackingTickInterval) {
          EntityType<T> entityType = Registry.register(Registries.ENTITY_TYPE, new Identifier(Lulasmod.MOD_ID, id),
                  EntityType.Builder.create(entityFactory, SpawnGroup.MISC)
                  .setDimensions(width, height)
                  .maxTrackingRange(maxTrackingRange)
                  .trackingTickInterval(trackingTickInterval)
                  .build(new Identifier(Lulasmod.MOD_ID, id).toString()));
         EntityRendererRegistry.register(entityType, entityRendererFactory);
         return entityType;
     }
     private static DefaultParticleType registerParticle(String name, Boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> render){
          DefaultParticleType particle = FabricParticleTypes.simple(alwaysShow);
          Registry.register(Registries.PARTICLE_TYPE, new Identifier(Lulasmod.MOD_ID, name), particle);
          ParticleFactoryRegistry.getInstance().register(particle, render);
          return particle;
     }
     private static Item registerItem(String name, Item item) {
         Items.CreativeTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
         return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
     }
     private static SpellItem registerSpell(String name, SpellItem item) {
          Spells.SpellTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
          return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
     }

     public static class DamageSources {
         public static DamageSource BLOODSUCKING(Entity attacker) {return getDamageSource(attacker.getWorld(), BLOODSUCKING);}
         public static RegistryKey<DamageType> BLOODSUCKING = registerDamageType("bloodsucking");

          private static DamageSource getDamageSource(World world, RegistryKey<DamageType> damageType){
             return new DamageSource(world.getRegistryManager()
                     .get(RegistryKeys.DAMAGE_TYPE)
                     .getEntry(damageType)
                     .orElseThrow());
         }

         public static void init(){}
     }

     public static class Gamerules {
          public static final GameRules.Key<GameRules.BooleanRule> NEW_DEATH_SYSTEM = registerGameRule("newDeathSystem", GameRules.Category.PLAYER, false);

          public static void init(){}
     }

     public static class Entities {
          public static final EntityType<SmokeBombEntity> SMOKE_BOMB = registerEntity(
               "smoke_bomb",
               SmokeBombEntity::new,
               FlyingItemEntityRenderer::new,
               0.25F, 0.25F, 4, 10);
          public static final EntityType<MalignityEntity> MALIGNITY = registerEntity(
               "malignity",
               MalignityEntity::new,
               FlyingItemEntityRenderer::new,
               1.0F, 1.0F, 4, 10);
          public static final EntityType<ParticleProjectileEntity> PARTICLE_PROJECTILE = registerEntity(
               "particle_projectile",
               ParticleProjectileEntity::new,
               ParticleProjectileEntityRenderer::new,
               0.5F, 0.5F, 4, 20);

          public static void init(){}
     }

     public static class ItemGroups {
         public static final ItemGroup LULAS_GROUP = registerItemGroup("lulasmod_group", Items.SMOKE_BOMB, Items.CreativeTabItems);
         public static final ItemGroup SPELLS_GROUP = registerItemGroup("lulasmod_spells", Spells.HOME_SPELL, Spells.SpellTabItems);

         public static  void init(){}
     }

     public static class Items {
         public static final List<Identifier> CreativeTabItems = new LinkedList<>() {};

         public static final Item MODIFIED_TNT       = registerItem("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)));
         public static final Item SMOKE_BOMB         = registerItem("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)));
         public static final Item HOME_BUTTON        = registerItem("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1).maxDamage(100)));
         public static final Item GOLDEN_TRIDENT     = registerItem("golden_trident",        new GoldenTridentItem(new Item.Settings().maxCount(1).maxDamage(500)));
         public static final Item SHARP_TOME         = registerItem("sharp_tome",            new SharpTomeItem(new Item.Settings().maxCount(1).maxDamage(640)));
         public static final Item LASCIVIOUSNESS     = registerItem("lasciviousness",        new LasciviousnessItem(new Item.Settings().maxCount(1).maxDamage(500)));
         public static final Item SPELL_BOOK         = registerItem("spell_book",            new SpellBookItem());

         public static final Item HELLISH_SEAL       = registerItem("hellish_seal",          new HellishSeal());
         public static final Item GOLDEN_SEAL        = registerItem("golden_seal",           new GoldenSeal());
         public static final Item BLOODSUCKING_SEAL  = registerItem("bloodsucking_seal",     new BloodsuckingSeal());

         public static void init() {}
     }

     public static class Particles {
         public static final DefaultParticleType SCRATCH = registerParticle("scratch", true, SweepAttackParticle.Factory::new);
         public static final DefaultParticleType BLOOD_FLAME = registerParticle("blood_flame", true, FlameParticle.Factory::new);
         public static final DefaultParticleType GOLDEN_SHIMMER = registerParticle("golden_shimmer", false, FlameParticle.Factory::new);
         public static final DefaultParticleType CURSED_BLOOD = registerParticle("cursed_blood", false, FlameParticle.Factory::new);

         public static void init(){}
     }

     public static class Spells {
          public static final List<Identifier> SpellTabItems = new LinkedList<>(){};

          public static final SpellItem SLASH_SPELL = registerSpell("treachery_judecca", new SpellItem(3, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
              for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))) {
                 if (entity instanceof LivingEntity livingEntity) ModMethods.applyBleed(livingEntity, (int) (120 * efficiencyMultiplier));
                 else entity.discard();}
              double x = -MathHelper.sin(player.getYaw() * (float) (Math.PI / 180.0));
              double y = MathHelper.cos(player.getYaw() * (float) (Math.PI / 180.0));
              world.spawnParticles(Particles.SCRATCH, player.getX() + x, player.getBodyY(0.5), player.getZ() + y, 0, x, 0.0, y, 0.0);
              world.playSound(null, player.getBlockPos(), ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 100.0f, 1f);
          }});
          public static final SpellItem FIRE_SPELL = registerSpell("malignity", new SpellItem(300) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(efficiencyMultiplier +2), 100)));
          }});
          public static final SpellItem DASH_SPELL = registerSpell("purloining", new SpellItem(0) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SLOWNESS)) {
                 Identifier id = TagManager.read(player, TagCategories.DASH_SPELL);
                 if (id == null){
                         id = new Identifier(Lulasmod.MOD_ID, String.valueOf(5 * cooldownMultiplier));
                         TagManager.put(player, TagCategories.DASH_SPELL, id);
                 }
                 String usages = id.getPath();
                 player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), (usages.equals("1")? 50 : 2));
                 TagManager.put(player, TagCategories.DASH_SPELL, new Identifier(Lulasmod.MOD_ID, String.valueOf(
                     (usages.equals("1")?
                             5 * cooldownMultiplier :
                             Math.min(5 * cooldownMultiplier, Integer.parseInt(usages)) - 1)
                     )));
                     player.addVelocity(player.getRotationVec(1).normalize().add(0, 0.25, 0));
                     player.velocityModified = true;
                     world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
                } else player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), 20);
          }});
          public static final SpellItem SMOKE_SPELL = registerSpell("guile", new SpellItem(20) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              if (!player.hasStatusEffect(StatusEffects.CUSHIONED)) {
                  world.playSound(null, player.getBlockPos(), ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);
                  world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player.getPos().x, player.getPos().y + 1.0d, player.getPos().z, 269, 1.2d, 1.2d, 1.2d, 0d);
                  player.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, 600, 0, false, false));
                  player.addStatusEffect(new StatusEffectInstance(StatusEffects.CUSHIONED, Math.round(efficiencyMultiplier) *1200, 0, false, false));
              }
          }});
          public static final SpellItem HEAL_SPELL = registerSpell("appeasing", new SpellItem(300) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              player.setHealth(player.getMaxHealth());
          }});
          public static final SpellItem BLOOD_SPELL = registerSpell("emulations", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
             if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity victim)
                 ModMethods.applyBleed(victim, (int) (1200 * efficiencyMultiplier) -80);
             ModMethods.impale(player, player.getStackInHand(hand).getItem(), 20, 600, 6, Particles.CURSED_BLOOD);
          }});
          public static final SpellItem HOME_SPELL = registerSpell("wickedness", new SpellItem(600) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              ModMethods.sendHome(player, player.getStackInHand(hand).getItem());
          }});
          public static final SpellItem POCKET_SPELL = registerSpell("heresies", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              List<Entity> entities = world.getOtherEntities(player, new Box(player.getPos().add(-5d, -5d, -5d), player.getPos().add(5d, 5d, 5d)));
              if (entities.isEmpty()) entities.add(player);
              for (Entity victim : entities) {
                 world.spawnParticles(ParticleTypes.PORTAL, victim.getX(), victim.getY() + 0.5, victim.getZ(), 50, 0, 0, 0, 1);
                 if (!victim.teleport(Objects.requireNonNull(victim.getServer()).getWorld((
                     world.getRegistryKey().equals(Dimensions.POCKET_DIMENSION)?
                     World.OVERWORLD :
                     Dimensions.POCKET_DIMENSION
                 )), victim.getX(), victim.getY(), victim.getZ(), EnumSet.noneOf(PositionFlag.class), victim.getYaw(), victim.getPitch()))
                     Lulasmod.LOGGER.error("Could not perform teleport. Registry key: {}, Entity: {}", Dimensions.POCKET_DIMENSION, victim);
              }
          }});
          public static final SpellItem HIGHLIGHTER_SPELL = registerSpell("highlighter_spell", new SpellItem(0) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              boolean playerGlowing = !player.isGlowing();
              world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
              for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(playerGlowing);}
          }});

          public static void init() {}
     }

     public static class StatusEffects {
         public static final StatusEffect CUSHIONED = registerStatusEffect("cushioned", new CushionedStatusEffect());
         public static final StatusEffect BLEEDING = registerStatusEffect("bleeding", new BleedingStatusEffect());

         public static void init(){}
     }

     public static class Dimensions {
          public static final RegistryKey<World> POCKET_DIMENSION =  RegistryKey.of(RegistryKeys.WORLD, new Identifier(Lulasmod.MOD_ID, "pocket_dimension"));
     }

     public static class TagCategories {
         public static final TagManager.TagCategory DAMAGE_DELAY = new TagManager.TagCategory("DamageDelay");
         public static final TagManager.TagCategory EQUIPPED_SPELLS = new TagManager.TagCategory("EquippedSpells");
         public static final TagManager.TagCategory DASH_SPELL = new TagManager.TagCategory("PurloiningSpell");
         public static final TagManager.TagCategory LASCIVIOUSNESS_TARGET = new TagManager.TagCategory("LasciviousnessTarget");
         public static final TagManager.TagCategory SPELL_BOOK_SPELLS = new TagManager.TagCategory("SpellBookSpells");
     }

     public static class Packets {
          public static final Identifier PLAYER_SPELL_LIST = new Identifier(Lulasmod.MOD_ID, "player_spell_list");
          public static final Identifier CYCLE_PLAYER_SPELL = new Identifier(Lulasmod.MOD_ID, "cycle_player_spell");
     }
}
