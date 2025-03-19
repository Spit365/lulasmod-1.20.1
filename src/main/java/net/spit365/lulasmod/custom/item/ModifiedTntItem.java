package net.spit365.lulasmod.custom.item;

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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModItems;

public class ModifiedTntItem extends Item {
    public ModifiedTntItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !player.getItemCooldownManager().isCoolingDown(this) && player.experienceLevel > 0){
            player.getItemCooldownManager().set(this, 7);
            Vec3d pos = player.raycast(1000, 1, false).getPos();
            world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), 4.0f, World.ExplosionSourceType.TNT);
            if (!player.isCreative()) {
                player.addExperienceLevels(-1);
                player.getStackInHand(hand).decrement(1);
            }
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
