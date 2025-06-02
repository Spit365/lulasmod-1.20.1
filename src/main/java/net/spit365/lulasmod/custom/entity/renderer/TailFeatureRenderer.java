package net.spit365.lulasmod.custom.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.LinkedList;

@Environment(EnvType.CLIENT)
public class TailFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public TailFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {super(context);}

	public static final LinkedList<String> tailedPlayerList = new LinkedList<>();

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l) {
		if (tailedPlayerList.contains(abstractClientPlayerEntity.getUuidAsString())
			&& abstractClientPlayerEntity.hasSkinTexture()
			&& !abstractClientPlayerEntity.isInvisible()
		) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(TailModel.TEXTURE));
            float o = MathHelper.lerp(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
			matrixStack.push();
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(o));
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-o));
			TailModel.getAnimatedTexturedModelData().createModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(abstractClientPlayerEntity, 0f), 1f, 1f, 1f, 1f);
			matrixStack.pop();
		}
	}
}
