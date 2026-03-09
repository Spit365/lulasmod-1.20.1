package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.entity.AmethystShardEntity;
@Environment(EnvType.CLIENT)
public class AmethystShardEntityRenderer extends EntityRenderer<AmethystShardEntity> {
    public static final Identifier TEXTURE = Identifier.of(Lulasmod.MOD_ID, "textures/entity/amethyst_shard.png");

    public AmethystShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(AmethystShardEntity entity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch())));

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
        matrixStack.scale(0.05625F, 0.05625F, 0.05625F);
        matrixStack.translate(-4.0F, 0.0F, 0.0F);

        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(entity)));
        MatrixStack.Entry entry = matrixStack.peek();

        // Drawing the "box" and the "cross" faces manually
        this.drawVertex(entry, vertexConsumer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
        // ... (repeat for all vertex calls from your snippet)

        matrixStack.pop();
        super.render(entity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    public void drawVertex(MatrixStack.Entry matrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        vertexConsumer.vertex(matrix, (float)x, (float)y, (float)z)
            .color(255, 255, 255, 255)
            .texture(u, v)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal(matrix, (float)normalX, (float)normalY, (float)normalZ);
    }

    @Override
    public Identifier getTexture(AmethystShardEntity entity) {
        return TEXTURE;
    }
}