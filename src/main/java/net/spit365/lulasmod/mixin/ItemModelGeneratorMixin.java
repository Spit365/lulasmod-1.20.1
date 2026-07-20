package net.spit365.lulasmod.mixin;

import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.item.Item;
import net.spit365.lulasmod.mod.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemModelGenerators.class)
public abstract class ItemModelGeneratorMixin {
    @Shadow public abstract void generateTrident(Item item);

    @Inject(method = "run()V", at = @At("TAIL"))
    private void register(CallbackInfo ci) {
        this.generateTrident(ModItems.GOLDEN_TRIDENT);
    }
}
