package net.spit365.lulasmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModEntities;

public class NeedleSwordItem extends SwordItem {
    public NeedleSwordItem(Settings settings) {
        super(ToolMaterials.NETHERITE, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld){
            ItemStack sword = user.getStackInHand(hand);
            world.spawnEntity(new NeedleSwordEntity(ModEntities.NEEDLE_SWORD, user, world, sword.copy()));
            sword.decrementUnlessCreative(1, user);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
