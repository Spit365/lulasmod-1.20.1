package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Objects;

public class SharpTomeItem extends Item{
    public SharpTomeItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient() && (getPaper(player) != null || player.isCreative())){
            player.getItemCooldownManager().set(this, 5);
            ArrowEntity arrow = new ArrowEntity(world, player);
            world.spawnEntity(arrow);
            arrow.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            arrow.addVelocity(player.getRotationVec(1).normalize().multiply(2));
            if (!player.isCreative()){
                Objects.requireNonNull(getPaper(player)).decrement(1);
                player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    private static ItemStack getPaper(PlayerEntity player){
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.getItem() == Items.PAPER) {
                return itemStack;
            }
        }
        return null;
    }
}