package net.spit365.lulasmod.mixin;

import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.item.Item;
import net.spit365.lulasmod.mod.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelGenerator.class)
public abstract class ItemModelGeneratorMixin {
    @Shadow public abstract void registerTrident(Item item);

    @Inject(method = "register()V", at = @At("TAIL"))
    private void register(CallbackInfo ci) {
        this.registerTrident(ModItems.GOLDEN_TRIDENT);
    }
}
