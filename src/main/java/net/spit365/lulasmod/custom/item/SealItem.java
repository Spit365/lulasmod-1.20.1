package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.spell.SpellManager;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModImportant;
import net.spit365.lulasmod.mod.ModItems;
import java.util.Objects;
import java.util.Set;
import static net.minecraft.sound.SoundEvents.*;
import static net.spit365.lulasmod.mod.ModImportant.PocketDimensionKey;

public class SealItem extends Item {
    public SealItem(Settings settings) {super(settings);}

    private static Item getSpell(PlayerEntity player) {
        return SpellManager.getSpells(player).get(0).getItem();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient() &&  !player.getItemCooldownManager().isCoolingDown(this)) {
            if (getSpell(player) == ModItems.FLAME_INCANTATION) {
                player.getItemCooldownManager().set(this, 3);
                Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getPos().add(0 ,1, 0));
                ((ServerWorld) world).spawnParticles(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), 50, 0.5, 0.5, 0.5, 0);
                world.playSound(null, player.getBlockPos(), ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 100.0f, 10.0f);
                for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))){
                    entity.damage(ModDamageSources.DIABLOS_FLAME(player), 1);
                }
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (getSpell(player) == ModItems.HOME_INCANTATION){
                player.getItemCooldownManager().set(this, 600);
                BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
                if (pos == null){pos = world.getSpawnPos();}
                player.teleport( pos.getX(), pos.getY() + 1, pos.getZ(), true);
                Lulasmod.LOGGER.info("{} was sent home to {} {} {} (with incantation)", player.getName().getString(), pos.getX(), pos.getY(), pos.getZ());
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (getSpell(player) == ModItems.SMOKE_INCANTATION){
                player.getItemCooldownManager().set(this, 5);
                ModImportant.summonSmoke(player.getPos(), world);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 1200, 1, false, false));
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (getSpell(player) == ModItems.HIGHLIGHTER_INCANTATION) {
                boolean isPlayerGlowing = !player.isGlowing();
                world.playSound(null, player.getBlockPos(), (isPlayerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
                for (PlayerEntity playerEntity : world.getPlayers()){playerEntity.setGlowing(isPlayerGlowing);}
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (getSpell(player) == ModItems.POCKET_INCANTATION){
                if (world.getRegistryKey().toString().equals(PocketDimensionKey)) {
                    player.teleport(Objects.requireNonNull(player.getServer()).getWorld(World.OVERWORLD), player.getX(), player.getY(), player.getZ(), Set.of() , player.getYaw(), player.getPitch());
                }else{
                    RegistryKey<World> worldRegistryKey = null;
                    for (RegistryKey<World> worldKeys : Objects.requireNonNull(player.getServer()).getWorldRegistryKeys()){if (
                            worldKeys.toString().equals(PocketDimensionKey)) worldRegistryKey = worldKeys;
                    }
                    if (worldRegistryKey == null) Lulasmod.LOGGER.error("could not find registry key for 'lulasmod:pocket_dimension'");
                    else player.teleport(player.getServer().getWorld(worldRegistryKey), player.getX(), player.getY(), player.getZ(), Set.of() , player.getYaw(), player.getPitch());
                }
                player.getItemCooldownManager().set(this, 20);
                return TypedActionResult.success(player.getStackInHand(hand));
            }
            if (getSpell(player) == ModItems.DASH_INCANTATION){
                player.addVelocity(player.getRotationVec(1).normalize().multiply(0.5));
                player.velocityModified = true;
                SpellManager.managePlayerSpellUsages(player, this, 15, 2, 50);

            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
