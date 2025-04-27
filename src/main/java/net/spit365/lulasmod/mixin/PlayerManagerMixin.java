package net.spit365.lulasmod.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @ModifyVariable(method = "respawnPlayer", at = @At("STORE"), ordinal = 1)
    public ServerWorld serverWorld(ServerWorld serverWorld) {
        return ((PlayerManager) (Object) this).getServer().getWorld(World.NETHER);
    }
    @Inject(method = "respawnPlayer", at = @At("TAIL"))
    public void respawnPlayer(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        World world = player.getWorld();
        world.setBlockState(player.getBlockPos(), Blocks.AIR.getDefaultState());
        world.setBlockState(player.getBlockPos().add(0, 1, 0), Blocks.AIR.getDefaultState());
    }
}
