package net.spit365.lulasmod.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModEntities;

public class NeedleSwordItem extends Item {
    public NeedleSwordItem(Properties settings) {
        super(settings.sword(ToolMaterial.NETHERITE, 3f, -2.4f));
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (world instanceof ServerLevel) {
            ItemStack sword = user.getItemInHand(hand);
            world.addFreshEntity(new NeedleSwordEntity(ModEntities.NEEDLE_SWORD, user, world, sword.copy()));
            sword.consume(1, user);
        }
        return InteractionResult.PASS;
    }
}
