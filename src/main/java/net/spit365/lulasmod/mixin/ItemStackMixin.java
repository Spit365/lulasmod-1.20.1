package net.spit365.lulasmod.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.mod.ModItems;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow public abstract boolean is(Item item);
    @Shadow public abstract @Nullable Entity getEntityRepresentation();

    @Inject(method = "isDamageableItem", at = @At("HEAD"), cancellable = true)
    private void isDamageable(CallbackInfoReturnable<Boolean> cir) {
        if (this.is(ModItems.SINFUL) && Demon.isDemon(this.getEntityRepresentation())) cir.setReturnValue(false);
    }
}
