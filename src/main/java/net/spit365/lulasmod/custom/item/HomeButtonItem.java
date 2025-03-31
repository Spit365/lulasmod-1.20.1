package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;

public class HomeButtonItem extends Item {
    public HomeButtonItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()) {
            player.getItemCooldownManager().set(this, 6000);
            BlockPos pos = ((ServerPlayerEntity) player).getSpawnPointPosition();
            if (pos == null){pos = world.getSpawnPos();}
            player.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
            Lulasmod.LOGGER.info("{} was sent home to {} {} {} (with button)", player.getName(), pos.getX(), pos.getY(), pos.getZ());
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
