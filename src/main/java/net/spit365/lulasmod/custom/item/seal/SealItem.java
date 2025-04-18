package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.FlameSlingEntity;
import net.spit365.lulasmod.custom.item.IncantationItem;
import net.spit365.lulasmod.mod.*;
import net.spit365.lulasmod.tag.TagManager;
import java.util.Objects;
import java.util.Set;
import static net.minecraft.sound.SoundEvents.*;
import static net.spit365.lulasmod.mod.ModImportant.PocketDimensionKey;

public abstract class SealItem extends Item {
    public SealItem(Settings settings) {
        super(settings);
    }

    protected static String getSpell(PlayerEntity player) {
        return TagManager.readList(player, ModTagCategories.SPELLS).get(0);
    }

    protected abstract Boolean canUse(PlayerEntity player);
    protected abstract Float efficiencyMultiplier();
    protected abstract Integer cooldownMultiplier();

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient() && canUse(player)) {
            if (getSpell(player).equals(((IncantationItem)ModItems.FROST_INCANTATION).getSpellName())) {
                player.getItemCooldownManager().set(this, 3 / cooldownMultiplier());
                Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getPos().add(0 ,1, 0));
                for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))){
                    if (!(entity instanceof LivingEntity)) entity.discard();
                    entity.damage(ModDamageSources.ETERNAL_WINTER(player), efficiencyMultiplier() * 2);}
                ((ServerWorld) world).spawnParticles(ParticleTypes.SNOWFLAKE, pos.getX(), pos.getY(), pos.getZ(), 50, 0.5, 0.5, 0.5, 0);
                world.playSound(null, player.getBlockPos(), ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 100.0f, 10.0f);
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.HOME_INCANTATION).getSpellName())){
                player.getItemCooldownManager().set(this, 600 / cooldownMultiplier());
                BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
                if (pos == null){pos = world.getSpawnPos();}
                player.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                Lulasmod.LOGGER.info("{} was sent home to {} {} {} (with incantation)", player.getName().getString(), pos.getX(), pos.getY(), pos.getZ());
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.SMOKE_INCANTATION).getSpellName())){
                player.getItemCooldownManager().set(this, 5 / cooldownMultiplier());
                world.playSound(null, player.getBlockPos(), ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);
                ModImportant.summonSmoke(player.getPos(), world);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 600, 1, false, false));
                if (efficiencyMultiplier() > 1) player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 1200, Math.min(Math.round(efficiencyMultiplier()) -1, 254), false, false));
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.HIGHLIGHTER_INCANTATION).getSpellName())) {
                boolean isPlayerGlowing = !player.isGlowing();
                world.playSound(null, player.getBlockPos(), (isPlayerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
                for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(isPlayerGlowing);}
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.POCKET_INCANTATION).getSpellName())){
                if (world.getRegistryKey().toString().equals(PocketDimensionKey)) {
                    player.teleport(Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD), player.getX(), player.getY(), player.getZ(), Set.of() , player.getYaw(), player.getPitch());
                }else{
                    RegistryKey<World> worldRegistryKey = null;
                    for (RegistryKey<World> worldKeys : Objects.requireNonNull(player.getServer()).getWorldRegistryKeys()){if (
                            worldKeys.toString().equals(PocketDimensionKey)) worldRegistryKey = worldKeys;
                    }
                    if (worldRegistryKey == null) Lulasmod.LOGGER.error("could not find registry key: " + PocketDimensionKey);
                    else player.teleport(player.getServer().getWorld(worldRegistryKey), player.getX(), player.getY(), player.getZ(), Set.of() , player.getYaw(), player.getPitch());
                }
                player.getItemCooldownManager().set(this, 20 / cooldownMultiplier());
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.DASH_INCANTATION).getSpellName())){
                if (!player.hasStatusEffect(StatusEffects.SLOWNESS)) {
                    String read = TagManager.read(player, ModTagCategories.DASH_SPELL);
                    if (read == null) TagManager.put(player, ModTagCategories.DASH_SPELL, String.valueOf(5 * cooldownMultiplier()));
                    if (read.equals("1")){
                        player.getItemCooldownManager().set(this, 50);
                        TagManager.put(player, ModTagCategories.DASH_SPELL, String.valueOf(5 * cooldownMultiplier()));
                    }else {
                        player.getItemCooldownManager().set(this, 2);
                        TagManager.put(player, ModTagCategories.DASH_SPELL, String.valueOf(Math.min(5 * cooldownMultiplier(), Integer.parseInt(read)) -1));
                    }
                    player.addVelocity(player.getRotationVec(1).normalize().add(0 , 0.25, 0));
                    player.velocityModified = true;
                }else player.getItemCooldownManager().set(this, 20);
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.FIRE_INCANTATION).getSpellName())){
                player.getItemCooldownManager().set(this, 100 / cooldownMultiplier());
                Vec3d vec  = player.getRotationVec(1).normalize().multiply(3);
                FlameSlingEntity flameSling = new FlameSlingEntity(world, player, vec.getX(), vec.getY(), vec.getZ(), Math.min(Math.round(efficiencyMultiplier() +2), 100));
                flameSling.requestTeleport(flameSling.getX(), flameSling.getY() +1, flameSling.getZ());
                world.spawnEntity(flameSling);
            }
            if (getSpell(player).equals(((IncantationItem)ModItems.HEAL_INCANTATION).getSpellName())){
                player.getItemCooldownManager().set(this, 600 / cooldownMultiplier());
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, Math.round(efficiencyMultiplier()) *20, 100));
            }


            if (getSpell(player) != null){
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
