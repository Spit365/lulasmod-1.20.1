package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.block.SpellPedestalBlock;
import net.spit365.lulasmod.custom.effect.BleedingStatusEffect;
import net.spit365.lulasmod.custom.effect.CushionedStatusEffect;
import net.spit365.lulasmod.custom.entity.AmethystShardEntity;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.item.*;
import net.spit365.lulasmod.custom.item.seal.*;
import net.spit365.lulasmod.manager.*;

import java.util.*;
import static net.minecraft.sound.SoundEvents.*;
import static net.spit365.lulasmod.mod.ModMethods.impaled;

public class ModServer {
     public record BlockAndItem(Block block, BlockItem item){}

     private static class register {
          private static GameRules.Key<GameRules.BooleanRule> GameRule(String name, GameRules.Category category, boolean defaultValue) {
               return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
          }
          private static TagManager.TagCategory TagCategory(String name){
               return new TagManager.TagCategory(name);
          }
          private static RegistryKey<DamageType> DamageType(String name){
               return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Server.MOD_ID, name));
          }
          private static RegistryKey<World> Dimension(String name){
               return RegistryKey.of(RegistryKeys.WORLD, new Identifier(Server.MOD_ID, name));
          }
          private static StatusEffect StatusEffect(String id, StatusEffect entry) {
               return Registry.register(Registries.STATUS_EFFECT, new Identifier(Server.MOD_ID, id), entry);
          }
          private static ItemGroup ItemGroup(String name, Item item, List<Identifier> list) {
               return Registry.register(Registries.ITEM_GROUP, new Identifier(Server.MOD_ID, name), FabricItemGroup.builder()
                       .displayName(Text.translatable("item_group." + Server.MOD_ID + "." + name))
                       .icon(() -> new ItemStack(item))
                       .entries((displayContext, entries) -> {for (Identifier id : list) entries.add(Registries.ITEM.get(id));})
                       .build());
          }
          private static Item Item(String name, Item item) {
              Items.CreativeTabItems.add(Identifier.of(Server.MOD_ID, name));
              return Registry.register(Registries.ITEM, Identifier.of(Server.MOD_ID, name), item);
          }
          private static SpellItem Spell(String name, SpellItem item) {
               Spells.SpellTabItems.add(Identifier.of(Server.MOD_ID, name));
               return Registry.register(Registries.ITEM, Identifier.of(Server.MOD_ID, name), item);
          }
          private static BlockAndItem Block(String name, Block block) {
               Identifier id = new Identifier(Server.MOD_ID, name);
               Items.CreativeTabItems.add(id);
               return new BlockAndItem(
                       Registry.register(Registries.BLOCK, id, block),
                       Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()))
               );
          }
     }

     public static class DamageSources {
         public static DamageSource BLOODSUCKING(Entity attacker) {return getDamageSource(attacker.getWorld(), BLOODSUCKING);}
         public static DamageSource AMETHYST_SHARD(Entity attacker) {return getDamageSource(attacker.getWorld(), AMETHYST_SHARD);}
         public static final RegistryKey<DamageType> BLOODSUCKING = register.DamageType("bloodsucking");
         public static final RegistryKey<DamageType> AMETHYST_SHARD = register.DamageType("amethyst_shard");

         private static DamageSource getDamageSource(World world, RegistryKey<DamageType> damageType){
             return new DamageSource(world.getRegistryManager()
                     .get(RegistryKeys.DAMAGE_TYPE)
                     .getEntry(damageType)
                     .orElseThrow());
         }

         private static void init(){}
     }

     public static class Gamerules {
          public static final GameRules.Key<GameRules.BooleanRule> NEW_DEATH_SYSTEM = register.GameRule("newDeathSystem", GameRules.Category.PLAYER, false);

          private static void init(){}
     }

     public static class ItemGroups {
         public static final ItemGroup LULAS_GROUP = register.ItemGroup("lulasmod_group", Items.SMOKE_BOMB, Items.CreativeTabItems);
         public static final ItemGroup SPELLS_GROUP = register.ItemGroup("lulasmod_spells", Spells.HOME_SPELL, Spells.SpellTabItems);

         private static  void init(){}
     }

     public static class Items {
         public static final List<Identifier> CreativeTabItems = new LinkedList<>() {};

         public static final Item MODIFIED_TNT       = register.Item("modified_tnt",          new ModifiedTntItem());
         public static final Item SMOKE_BOMB         = register.Item("smoke_bomb",            new SmokeBombItem());
         public static final Item HOME_BUTTON        = register.Item("home_button",           new HomeButtonItem());
         public static final Item GOLDEN_TRIDENT     = register.Item("golden_trident",        new GoldenTridentItem());
         public static final Item SHARP_TOME         = register.Item("sharp_tome",            new SharpTomeItem());
         public static final Item SINFUL             = register.Item("sinful",                new SinfulItem());
         public static final Item SPELL_BOOK         = register.Item("spell_book",            new SpellBookItem());

         public static final Item SEAL               = register.Item("seal", new StandartSeal());
         public static final Item HELLISH_SEAL       = register.Item("hellish_seal", new HellishSeal());
         public static final Item GOLDEN_SEAL        = register.Item("golden_seal", new GoldenSeal());
         public static final Item BLOODSUCKING_SEAL  = register.Item("bloodsucking_seal", new BloodsuckingSeal());

         public static final List<Item> tailedExclusive = List.of(ModServer.Items.HELLISH_SEAL, ModServer.Spells.SLASH_SPELL, ModServer.Spells.BLOOD_SPELL, ModServer.Spells.POCKET_SPELL);

         private static void init() {}

     }

     public static class Spells {
          public static final List<Identifier> SpellTabItems = new LinkedList<>(){};

          public static final SpellItem SLASH_SPELL = register.Spell("treachery_judecca", new SpellItem(3, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
              for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))) {
                 if (entity instanceof LivingEntity livingEntity) ModMethods.applyBleed(livingEntity, (int) (120 * efficiencyMultiplier));
                 else entity.discard();}
              world.spawnParticles(ModClient.Particles.SCRATCH, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0);
              world.playSound(null, player.getBlockPos(), ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 100.0f, 1f);
          }});
          public static final SpellItem FIRE_SPELL = register.Spell("malignity", new SpellItem(300) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(efficiencyMultiplier +2), 100)));
          }});
          public static final SpellItem DASH_SPELL = register.Spell("purloining", new SpellItem(0) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SLOWNESS)) {
                 Identifier id = TagManager.read(player, TagCategories.DASH_SPELL);
                 if (id == null){
                         id = new Identifier(Server.MOD_ID, String.valueOf(5 * cooldownMultiplier));
                         TagManager.put(player, TagCategories.DASH_SPELL, id);
                 }
                 String usages = id.getPath();
                 player.getItemCooldownManager().set(player.getStackInHand(hand).getItem(), (usages.equals("1")? (player.isOnGround()? 20 : 40) : 5));
                 TagManager.put(player, TagCategories.DASH_SPELL, new Identifier(Server.MOD_ID, String.valueOf(
                     (usages.equals("1")?
                             5 * cooldownMultiplier :
                             Math.min(5 * cooldownMultiplier, Integer.parseInt(usages)) - 1)
                     )));
                player.addVelocity(player.getRotationVec(1).normalize().add(0, 0.25, 0));
                player.velocityModified = true;
                player.fallDistance = 0;
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
               player.getHungerManager().add(100, 0f);
          }});
          public static final SpellItem BLOOD_SPELL = register.Spell("emulations", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
             if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity victim)
                 ModMethods.applyBleed(victim, (int) (1200 * efficiencyMultiplier) -80);
             ModMethods.impale(player, player.getStackInHand(hand).getItem(), 20, 600, 6, ModClient.Particles.CURSED_BLOOD);
          }});
          public static final SpellItem HOME_SPELL = register.Spell("wickedness", new SpellItem(600) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              ModMethods.sendHome(player, player.getStackInHand(hand).getItem());
          }});
          public static final SpellItem POCKET_SPELL = register.Spell("heresies", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              List<Entity> entities = world.getOtherEntities(player, new Box(player.getPos().add(-5d, -5d, -5d), player.getPos().add(5d, 5d, 5d)));
              entities.removeIf(entity -> TagManager.read(entity, TagCategories.TIME_FORWARD_ANIMATION_FRAMES) != null);
              if (entities.isEmpty()) entities.add(player);
              for (Entity victim : entities) {
                 world.spawnParticles(ParticleTypes.PORTAL, victim.getX(), victim.getY() + 0.5, victim.getZ(), 50, 0, 0, 0, 1);
                 if (world.getRegistryKey().equals(World.OVERWORLD) && victim instanceof ServerPlayerEntity serverPlayer){
                    ServerPlayNetworking.send(serverPlayer, Packets.TIME_FORWARD_ANIMATION, PacketByteBufs.create());
                    TagManager.put(victim, TagCategories.TIME_FORWARD_ANIMATION_FRAMES, new Identifier(Server.MOD_ID, "450"));
                 } else ModMethods.pocketTeleport(victim);
              }
          }});
          public static final SpellItem AMETHYST_SPELL = register.Spell("envy", new SpellItem(20) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
               AmethystShardEntity amethystShardEntity = new AmethystShardEntity(player, world);
               amethystShardEntity.addVelocity(player.getRotationVec(1).normalize().multiply(5));
               amethystShardEntity.setDamage(8);
               world.spawnEntity(amethystShardEntity);
          }});
          public static final SpellItem HIGHLIGHTER_SPELL = register.Spell("highlighter_spell", new SpellItem(0) {@Override public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
              boolean playerGlowing = !player.isGlowing();
              world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
              for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(playerGlowing);}
          }});

          private static void init() {}
     }

     public static class Blocks {
          public static final BlockAndItem SPELL_PEDESTAL = register.Block(
                  "spell_pedestal",
                  new SpellPedestalBlock(
                          AbstractBlock.Settings.create()
                                  .mapColor(MapColor.STONE_GRAY)
                                  .strength(-1.0F, Float.MAX_VALUE)
                                  .dropsNothing()
                                  .allowsSpawning(net.minecraft.block.Blocks::never)));

          private static void init() {}
     }

     public static class StatusEffects {
         public static final StatusEffect CUSHIONED = register.StatusEffect("cushioned", new CushionedStatusEffect());
         public static final StatusEffect BLEEDING = register.StatusEffect("bleeding", new BleedingStatusEffect());

          private static void init(){}
     }

     public static class Dimensions {
          public static final RegistryKey<World> POCKET_DIMENSION = register.Dimension("pocket_dimension");
     }

     public static class TagCategories {
         public static final TagManager.TagCategory DAMAGE_DELAY = register.TagCategory("DamageDelay");
         public static final TagManager.TagCategory EQUIPPED_SPELLS = register.TagCategory("EquippedSpells");
         public static final TagManager.TagCategory DASH_SPELL = register.TagCategory("PurloiningSpell");
         public static final TagManager.TagCategory ABSORBED_PEDESTALS = register.TagCategory("AbsorbedPedestals");
         public static final TagManager.TagCategory TIME_FORWARD_ANIMATION_FRAMES = register.TagCategory("TimeForwardAnimationFrames");
     }

     public static class Packets {
          public static final Identifier SPELL_HOTBAR_LIST = new Identifier(Server.MOD_ID, "spell_hotbar_list");
          public static final Identifier CYCLE_PLAYER_SPELL = new Identifier(Server.MOD_ID, "cycle_player_spell");
          public static final Identifier TAILED_PLAYER_LIST = new Identifier(Server.MOD_ID, "tailed_player_list");
          public static final Identifier TIME_FORWARD_ANIMATION = new Identifier(Server.MOD_ID, "time_forward_animation");
     }

     public static class Tickers {
          private static int sporesCounter = 0;
          private static int impaledCounter = 0;
          public record ImpaledContext(PlayerEntity player, LivingEntity livingEntity, ParticleEffect particle, Integer iterations) {
               public ImpaledContext(ImpaledContext context, Integer iterations) {
                    this(context.player(), context.livingEntity(), context.particle(), iterations);
               }
          }
          private static void sendSpellListPacket(ServerPlayerEntity player, List<Identifier> list) {
               Map<Integer, ItemStack> map = new HashMap<>();
               for (int i = 0; i < list.size(); i++)
                    map.put(i, new ItemStack(Registries.ITEM.get(list.get(i))));
               PacketByteBuf buf = PacketByteBufs.create();
               buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeItemStack);
               ServerPlayNetworking.send(player, ModServer.Packets.SPELL_HOTBAR_LIST, buf);
          }

          public static final TickerManager.Ticker<MinecraftServer> repelMiner = TickerManager.createTicker(MinecraftServer.class, input -> {
               for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList()) {
                    if (player.getCommandTags().contains("miner")) {
                         BlockPos playerPos = player.getBlockPos();
                         BlockPos closestPortal = null;
                         for (BlockPos pos : BlockPos.stream(
                                 playerPos.add(-5, -5, -5),
                                 playerPos.add(5, 5, 5)
                         ).map(BlockPos::toImmutable).toList())
                              if ((
                                   player.getWorld().getBlockState(pos).isOf(net.minecraft.block.Blocks.END_PORTAL) ||
                                   player.getWorld().getBlockState(pos).isOf(net.minecraft.block.Blocks.NETHER_PORTAL)) &&
                                   (closestPortal == null || pos.getSquaredDistance(playerPos) < closestPortal.getSquaredDistance(playerPos)
                              )) closestPortal = pos;
                         if (closestPortal != null) {
                              ModMethods.outlineBox(new Box(closestPortal.add(-5, -5, -5), closestPortal.add(5, 5, 5)), player.getServerWorld(), ModClient.Particles.GOLDEN_SHIMMER);
                              player.setVelocity(player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize());
                              player.velocityModified = true;
                         }
                    }
               }
          });
          public static final TickerManager.Ticker<MinecraftServer> updateSpells = TickerManager.createTicker(MinecraftServer.class, input -> {
               for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList()) {
                    if(player.getMainHandStack().getItem() instanceof SpellHotbar item) sendSpellListPacket(player, item.displayList(player));
                    else if(player.getOffHandStack().getItem() instanceof SpellHotbar item) sendSpellListPacket(player, item.displayList(player));
               }
          });
          public static final TickerManager.Ticker<Void> updateImpaled = TickerManager.createTicker(Void.class, input -> {
               impaledCounter++;
               for (ImpaledContext context : impaled) {
                    LivingEntity victim = context.livingEntity;
                    if (context.iterations > 0 && victim.isAlive()) {
                         if (victim instanceof EndermanEntity) victim.kill();
                         victim.setVelocity(0, 0, 0);
                         if (impaledCounter >= 25) {
                              impaledCounter = 0;
                              double radius = 5;
                              Vec3d pos = new Vec3d(Math.random() * radius - radius / 2, Math.random() * radius - radius / 2, Math.random() * radius - radius / 2).normalize().multiply(radius).add(victim.getPos());
                              TagManager.put(context.player, ModServer.TagCategories.DAMAGE_DELAY, new Identifier(Server.MOD_ID, "0"));
                              victim.getWorld().spawnEntity(new ParticleProjectileEntity(
                                      victim.getWorld(), context.player, pos, pos.subtract(victim.getPos()).multiply(-0.5), context.particle));
                              impaled.remove(context);
                              impaled.add(new ImpaledContext(context, context.iterations - 1));
                         }
                    } else {
                         impaled.remove(context);
                         TagManager.remove(context.player, ModServer.TagCategories.DAMAGE_DELAY);
                         victim.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SLOW_FALLING, 50));
                    }
               }
          });
          public static final TickerManager.Ticker<MinecraftServer> updateTailedVisuals = TickerManager.createTicker(MinecraftServer.class, input -> {
               sporesCounter--;
               Map<Integer, String> tailedPlayers = new HashMap<>();
               input.getPlayerManager().getPlayerList().forEach(player -> {
                    if (player.getCommandTags().contains("tailed")) {
                         tailedPlayers.put(tailedPlayers.size(), player.getUuidAsString());
                         if (sporesCounter <= 0 && player.getWorld() instanceof ServerWorld world)
                              world.spawnParticles(ParticleTypes.CRIMSON_SPORE, player.getX(), player.getY() +1, player.getZ(), player.getRandom().nextBetweenExclusive(2, 4), 0, 0, 0, 0);
               }});
               if (sporesCounter <= 0) sporesCounter = new Random().nextInt(30, 60);
               PacketByteBuf buf = PacketByteBufs.create();
               buf.writeMap(tailedPlayers, PacketByteBuf::writeInt, PacketByteBuf::writeString);
               for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList())
                    ServerPlayNetworking.send(player, ModServer.Packets.TAILED_PLAYER_LIST, buf);
          });
          public static final TickerManager.Ticker<MinecraftServer> updateForwardAnimation = TickerManager.createTicker(MinecraftServer.class, input ->{
               for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList()){
                    Identifier read = TagManager.read(player, TagCategories.TIME_FORWARD_ANIMATION_FRAMES);
                    if (read != null) {
                         int i = Integer.parseInt(read.getPath());
                         if (i > 0) TagManager.put(player, TagCategories.TIME_FORWARD_ANIMATION_FRAMES, new Identifier(Server.MOD_ID, String.valueOf(i -1)));
                         else {
                              TagManager.remove(player, TagCategories.TIME_FORWARD_ANIMATION_FRAMES);
                              ModMethods.pocketTeleport(player);
                         }
                    }
               }
          });

          private static void init(){}
     }

     public static void init() {
          Items.init();
          Spells.init();
          Blocks.init();
          ItemGroups.init();
          DamageSources.init();
          StatusEffects.init();
          Gamerules.init();
          Tickers.init();
     }
}