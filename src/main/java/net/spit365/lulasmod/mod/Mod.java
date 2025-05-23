package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.Instrument;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.block.SpellPedestalBlock;
import net.spit365.lulasmod.custom.effect.BleedingStatusEffect;
import net.spit365.lulasmod.custom.effect.CushionedStatusEffect;
import net.spit365.lulasmod.custom.entity.AmethystShardEntity;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.entity.SmokeBombEntity;
import net.spit365.lulasmod.custom.entity.renderer.AmethystShardEntityRenderer;
import net.spit365.lulasmod.custom.entity.renderer.ParticleProjectileEntityRenderer;
import net.spit365.lulasmod.custom.item.*;
import net.spit365.lulasmod.custom.item.seal.BloodsuckingSeal;
import net.spit365.lulasmod.custom.item.seal.GoldenSeal;
import net.spit365.lulasmod.custom.item.seal.HellishSeal;
import net.spit365.lulasmod.tag.TagManager;
import java.util.*;
import static net.minecraft.sound.SoundEvents.*;

public class Mod {
     private record BlockAndItem(Block block, BlockItem item){}

     private static class register {
          private static GameRules.Key<GameRules.BooleanRule> GameRule(String name, GameRules.Category category, boolean defaultValue) {
               return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
          }
          private static TagManager.TagCategory TagCategory(String name){
               return new TagManager.TagCategory(name);
          }
          private static RegistryKey<DamageType> DamageType(String name){
               return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Lulasmod.MOD_ID, name));
          }
          private static RegistryKey<World> Dimension(String name){
               return RegistryKey.of(RegistryKeys.WORLD, new Identifier(Lulasmod.MOD_ID, name));
          }
          private static StatusEffect StatusEffect(String id, StatusEffect entry) {
               return Registry.register(Registries.STATUS_EFFECT, new Identifier(Lulasmod.MOD_ID, id), entry);
          }
          private static ItemGroup ItemGroup(String name, Item item, List<Identifier> list) {
               return Registry.register(Registries.ITEM_GROUP, new Identifier(Lulasmod.MOD_ID, name), FabricItemGroup.builder()
                       .displayName(Text.translatable("item_group." + Lulasmod.MOD_ID + "." + name))
                       .icon(() -> new ItemStack(item))
                       .entries((displayContext, entries) -> {for (Identifier id : list) entries.add(Registries.ITEM.get(id));})
                       .build());
          }
          private static <T extends Entity> EntityType<T> Entity(String id, EntityType.EntityFactory<T> entityFactory, EntityRendererFactory<T> entityRendererFactory, float width, float height, int maxTrackingRange, int trackingTickInterval) {
               EntityType<T> entityType = Registry.register(Registries.ENTITY_TYPE, new Identifier(Lulasmod.MOD_ID, id),
                       EntityType.Builder.create(entityFactory, SpawnGroup.MISC)
                       .setDimensions(width, height)
                       .maxTrackingRange(maxTrackingRange)
                       .trackingTickInterval(trackingTickInterval)
                       .build(new Identifier(Lulasmod.MOD_ID, id).toString()));
              EntityRendererRegistry.register(entityType, entityRendererFactory);
              return entityType;
          }
          private static DefaultParticleType Particle(String name, Boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<DefaultParticleType> render){
               DefaultParticleType particle = FabricParticleTypes.simple(alwaysShow);
               Registry.register(Registries.PARTICLE_TYPE, new Identifier(Lulasmod.MOD_ID, name), particle);
               ParticleFactoryRegistry.getInstance().register(particle, render);
               return particle;
          }
          private static Item Item(String name, Item item) {
              Items.CreativeTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
              return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
          }
          private static SpellItem Spell(String name, SpellItem item) {
               Spells.SpellTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
               return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
          }
          private static BlockAndItem Block(String name, Block block) {
               BlockItem item = Registry.register(Registries.ITEM, new Identifier(Lulasmod.MOD_ID, name), new BlockItem(block, new Item.Settings()));
               Items.CreativeTabItems.add(new Identifier(Lulasmod.MOD_ID, name));
               return new BlockAndItem(
                       Registry.register(Registries.BLOCK, new Identifier(Lulasmod.MOD_ID, name), block),
                       item
               );
          }
     }

     public static class DamageSources {
         public static DamageSource BLOODSUCKING(Entity attacker) {return getDamageSource(attacker.getWorld(), BLOODSUCKING);}
         public static DamageSource AMETHYST_SHARD(Entity attacker) {return getDamageSource(attacker.getWorld(), AMETHYST_SHARD);}
         public static RegistryKey<DamageType> BLOODSUCKING = register.DamageType("bloodsucking");
          private static final RegistryKey<DamageType> AMETHYST_SHARD = register.DamageType("amethyst_shard");

         private static DamageSource getDamageSource(World world, RegistryKey<DamageType> damageType){
             return new DamageSource(world.getRegistryManager()
                     .get(RegistryKeys.DAMAGE_TYPE)
                     .getEntry(damageType)
                     .orElseThrow());
         }

         public static void init(){}
     }

     public static class Gamerules {
          public static final GameRules.Key<GameRules.BooleanRule> NEW_DEATH_SYSTEM = register.GameRule("newDeathSystem", GameRules.Category.PLAYER, false);

          public static void init(){}
     }

     public static class Entities {
          public static final EntityType<SmokeBombEntity> SMOKE_BOMB = register.Entity(
               "smoke_bomb",
               SmokeBombEntity::new,
               FlyingItemEntityRenderer::new,
               0.25F, 0.25F, 4, 10);
          public static final EntityType<MalignityEntity> MALIGNITY = register.Entity(
               "malignity",
               MalignityEntity::new,
               FlyingItemEntityRenderer::new,
               1.0F, 1.0F, 4, 10);
          public static final EntityType<ParticleProjectileEntity> PARTICLE_PROJECTILE = register.Entity(
               "particle_projectile",
               ParticleProjectileEntity::new,
               ParticleProjectileEntityRenderer::new,
               0.5F, 0.5F, 4, 20);
          public static final EntityType<AmethystShardEntity> AMETHYST_SHARD = register.Entity(
               "amethyst_shard",
               AmethystShardEntity::new,
               AmethystShardEntityRenderer::new,
               0.5f, 0.5f, 4, 20);

          public static void init(){}
     }

     public static class ItemGroups {
         public static final ItemGroup LULAS_GROUP = register.ItemGroup("lulasmod_group", Items.SMOKE_BOMB, Items.CreativeTabItems);
         public static final ItemGroup SPELLS_GROUP = register.ItemGroup("lulasmod_spells", Spells.HOME_SPELL, Spells.SpellTabItems);

         public static  void init(){}
     }

     public static class Items {
         public static final List<Identifier> CreativeTabItems = new LinkedList<>() {};
          public static final List<Item> tailedExclusive = List.of(Mod.Items.HELLISH_SEAL, Mod.Spells.SLASH_SPELL, Mod.Spells.BLOOD_SPELL, Mod.Spells.POCKET_SPELL);

         public static final Item MODIFIED_TNT       = register.Item("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)));
         public static final Item SMOKE_BOMB         = register.Item("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)));
         public static final Item HOME_BUTTON        = register.Item("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1).maxDamage(100)));
         public static final Item GOLDEN_TRIDENT     = register.Item("golden_trident",        new GoldenTridentItem(new Item.Settings().maxCount(1).maxDamage(500)));
         public static final Item SHARP_TOME         = register.Item("sharp_tome",            new SharpTomeItem(new Item.Settings().maxCount(1).maxDamage(640)));
         public static final Item SINFUL             = register.Item("sinful",                new SinfulItem());
         public static final Item SPELL_BOOK         = register.Item("spell_book",            new SpellBookItem());

         public static final Item HELLISH_SEAL       = register.Item("hellish_seal",          new HellishSeal());
         public static final Item GOLDEN_SEAL        = register.Item("golden_seal",           new GoldenSeal());
         public static final Item BLOODSUCKING_SEAL  = register.Item("bloodsucking_seal",     new BloodsuckingSeal());

         public static void init() {}
     }

     public static class Particles {
         public static final DefaultParticleType SCRATCH = register.Particle("scratch", true, SweepAttackParticle.Factory::new);
         public static final DefaultParticleType GOLDEN_SHIMMER = register.Particle("golden_shimmer", false, FlameParticle.Factory::new);
         public static final DefaultParticleType CURSED_BLOOD = register.Particle("cursed_blood", false, FlameParticle.Factory::new);

         public static void init(){}
     }

     public static class Spells {
          public static final List<Identifier> SpellTabItems = new LinkedList<>(){};

          public static final SpellItem SLASH_SPELL = register.Spell("treachery_judecca", new SpellItem(3, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
              for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))) {
                 if (entity instanceof LivingEntity livingEntity) ModMethods.applyBleed(livingEntity, (int) (120 * efficiencyMultiplier));
                 else entity.discard();}
              world.spawnParticles(Particles.SCRATCH, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0);
              world.playSound(null, player.getBlockPos(), ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 100.0f, 1f);
          }});
          public static final SpellItem FIRE_SPELL = register.Spell("malignity", new SpellItem(300) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(efficiencyMultiplier +2), 100)));
          }});
          public static final SpellItem DASH_SPELL = register.Spell("purloining", new SpellItem(0) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
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
          public static final SpellItem SMOKE_SPELL = register.Spell("guile", new SpellItem(20) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               if (!player.hasStatusEffect(StatusEffects.CUSHIONED) && !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY)) {
                  world.playSound(null, player.getBlockPos(), ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);
                  world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player.getPos().x, player.getPos().y + 1.0d, player.getPos().z, 269, 1.2d, 1.2d, 1.2d, 0d);
                  player.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, Math.round(efficiencyMultiplier -1) *400, 0, false, false));
                  player.addStatusEffect(new StatusEffectInstance(StatusEffects.CUSHIONED, Math.round(efficiencyMultiplier -1) *1200, 0, false, false));
              }
          }});
          public static final SpellItem HEAL_SPELL = register.Spell("appeasing", new SpellItem(300) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              player.setHealth(player.getMaxHealth());
          }});
          public static final SpellItem BLOOD_SPELL = register.Spell("emulations", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
             if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity victim)
                 ModMethods.applyBleed(victim, (int) (1200 * efficiencyMultiplier) -80);
             ModMethods.impale(player, player.getStackInHand(hand).getItem(), 20, 600, 6, Particles.CURSED_BLOOD);
          }});
          public static final SpellItem HOME_SPELL = register.Spell("wickedness", new SpellItem(600) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              ModMethods.sendHome(player, player.getStackInHand(hand).getItem());
          }});
          public static final SpellItem POCKET_SPELL = register.Spell("heresies", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
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
          public static final SpellItem AMETHYST_SPELL = register.Spell("envy", new SpellItem(20) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               AmethystShardEntity amethystShardEntity = new AmethystShardEntity(player, world);
               amethystShardEntity.addVelocity(player.getRotationVec(1).normalize().multiply(5));
               amethystShardEntity.setDamage(5);
               world.spawnEntity(amethystShardEntity);
          }});
          public static final SpellItem HIGHLIGHTER_SPELL = register.Spell("highlighter_spell", new SpellItem(0) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              boolean playerGlowing = !player.isGlowing();
              world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
              for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(playerGlowing);}
          }});

          public static void init() {}
     }

     public static class Blocks {
          public static final BlockAndItem SPELL_PEDESTAL = register.Block(
                  "spell_pedestal",
                  new SpellPedestalBlock(
                          AbstractBlock.Settings.create()
                                  .mapColor(MapColor.STONE_GRAY)
                                  .instrument(Instrument.BASEDRUM)
                                  .strength(-1.0F, 3600000.0F)
                                  .dropsNothing()
                                  .allowsSpawning(net.minecraft.block.Blocks::never)
                  )
          );

          public static void init() {}
     }

     public static class StatusEffects {
         public static final StatusEffect CUSHIONED = register.StatusEffect("cushioned", new CushionedStatusEffect());
         public static final StatusEffect BLEEDING = register.StatusEffect("bleeding", new BleedingStatusEffect());

         public static void init(){}
     }

     public static class Dimensions {
          public static final RegistryKey<World> POCKET_DIMENSION = register.Dimension("pocket_dimension");
     }

     public static class TagCategories {
         public static final TagManager.TagCategory DAMAGE_DELAY = register.TagCategory("DamageDelay");
         public static final TagManager.TagCategory EQUIPPED_SPELLS = register.TagCategory("EquippedSpells");
         public static final TagManager.TagCategory DASH_SPELL = register.TagCategory("PurloiningSpell");
         public static final TagManager.TagCategory ABSORBED_PEDESTALS = register.TagCategory("absorbed_pedestals");
     }

     public static class Packets {
          public static final Identifier PLAYER_SPELL_LIST = new Identifier(Lulasmod.MOD_ID, "player_spell_list");
          public static final Identifier CYCLE_PLAYER_SPELL = new Identifier(Lulasmod.MOD_ID, "cycle_player_spell");
     }

     public static void init() {
          Items.init();
          Spells.init();
          Blocks.init();
          ItemGroups.init();
          Entities.init();
          DamageSources.init();
          StatusEffects.init();
          Particles.init();
          Gamerules.init();
     }
}