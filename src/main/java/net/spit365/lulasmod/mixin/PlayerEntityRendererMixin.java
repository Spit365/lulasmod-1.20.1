package net.spit365.lulasmod.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.entity.renderer.TailFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
     public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {super(ctx, model, shadowRadius);}
     @Override public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {return abstractClientPlayerEntity.getSkinTexture();}

     @Inject(at = @At("TAIL"), method = "<init>")
     public void init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
          addFeature(new TailFeatureRenderer((PlayerEntityRenderer) (Object) this));
     }
}
