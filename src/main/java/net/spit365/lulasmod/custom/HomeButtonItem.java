package net.spit365.lulasmod.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModItems;

public class HomeButtonItem extends Item {
    public HomeButtonItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !player.getItemCooldownManager().isCoolingDown(ModItems.HOME_BUTTON)) {
            player.getItemCooldownManager().set(ModItems.HOME_BUTTON, 6000);
            BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
            if (pos == null){pos = world.getSpawnPos();}
            player.teleport( pos.getX(), pos.getY() + 1, pos.getZ(), true);
            Lulasmod.LOGGER.info(player.getName() + " was sent home to " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " (with button)");
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
