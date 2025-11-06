package net.spit365.lulasmod.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModEntities;

public class NeedleSwordItem extends Item {
    public NeedleSwordItem(Settings settings) {
        super(settings.sword(ToolMaterial.NETHERITE, 3f, -2.4f));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (world instanceof ServerWorld){
            ItemStack sword = user.getStackInHand(hand);
            world.spawnEntity(new NeedleSwordEntity(ModEntities.NEEDLE_SWORD, user, world, sword.copy()));
            sword.decrementUnlessCreative(1, user);
        }
        return ActionResult.PASS;
    }
}
