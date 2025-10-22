package net.spit365.lulasmod.custom.entity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.entity.PinSwordEntity;

public class PinSwordEntityRenderer extends EntityRenderer<PinSwordEntity, PinSwordEntityRenderer.PinSwordRenderState> {
    private final ItemRenderer itemRenderer;

    public PinSwordEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    }

    @Override
    public PinSwordRenderState createRenderState() {
        return new PinSwordRenderState();
    }

    @Override
    public void render(PinSwordRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-state.pitch + 75));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-state.yaw));

        itemRenderer.renderItem(
            state.pinSword,
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            light,
            OverlayTexture.DEFAULT_UV,
            matrices,
            vertexConsumers,
            state.world,
            0
        );

        matrices.pop();
    }

    @Override
    public void updateRenderState(PinSwordEntity entity, PinSwordRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.pinSword = entity.getSword();
        state.world = entity.getWorld();
        state.yaw = entity.getYaw();
        state.pitch = entity.getPitch();
    }

    public static class PinSwordRenderState extends ProjectileEntityRenderState {
        public ItemStack pinSword;
        public World world;

    }
}
