package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Objects;

public class ModifiedTntItem extends Item {
    public ModifiedTntItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !player.getItemCooldownManager().isCoolingDown(this) && player.experienceLevel > 0){
            player.getItemCooldownManager().set(this, 7);
            TntEntity tnt = new TntEntity(EntityType.TNT, world);
            world.spawnEntity(tnt);
            tnt.teleport(player.getX(), player.getY(), player.getZ());
            tnt.setFuse(20);
            tnt.setVelocity(player.getRotationVec(1).normalize().multiply(5));
            if (!player.isCreative()) {
                player.addExperienceLevels(-1);
                player.getStackInHand(hand).decrement(1);
            }
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
