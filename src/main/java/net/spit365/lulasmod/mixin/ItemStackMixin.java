package net.spit365.lulasmod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.item.ShimmerSyringeItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean isOf(Item item);
    @Shadow public abstract @Nullable Entity getHolder();

    @Inject(method = "isDamageable", at = @At("HEAD"), cancellable = true)
    private void isDamageable(CallbackInfoReturnable<Boolean> cir) {
        if (this.isOf(ModItems.SINFUL) && Demon.isDemon(this.getHolder())) cir.setReturnValue(false);
    }
}
