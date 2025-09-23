package net.spit365.lulasmod.mixin;

import net.minecraft.client.render.entity.ItemEntityRenderer;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;


@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin {

    @ModifyVariable(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("STORE"), ordinal = 3)
    private float d(float d){
        return 0;
    }
    @ModifyVariable(method = "render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("STORE"), ordinal = 5)
    private float f(float f, ItemEntity entity){
        return entity.getYaw();
    }
}
