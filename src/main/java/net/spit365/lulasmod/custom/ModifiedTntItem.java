package net.spit365.lulasmod.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModItems;

public class ModifiedTntItem extends Item {
    public ModifiedTntItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !player.getItemCooldownManager().isCoolingDown(ModItems.MODIFIED_TNT)){
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
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
