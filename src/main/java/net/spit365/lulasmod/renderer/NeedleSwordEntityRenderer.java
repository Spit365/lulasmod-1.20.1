package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.entity.NeedleSwordEntity;
import net.spit365.lulasmod.mod.ModItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class NeedleSwordEntityRenderer extends EntityRenderer<NeedleSwordEntity, NeedleSwordEntityRenderer.NeedleSwordRenderState> {
    private static final Identifier needleHeadIdentifier = BuiltInRegistries.ITEM.getKey(ModItems.NEEDLE_HEAD);

    public NeedleSwordEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public NeedleSwordRenderState createRenderState() {
        return new NeedleSwordRenderState();
    }

    @Override
    public void submit(NeedleSwordRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(-state.xRot + 75));
        poseStack.mulPose(Axis.YP.rotationDegrees(-state.yRot));

	    Minecraft client = Minecraft.getInstance();
        ItemModel model = client.getModelManager().getItemModel(needleHeadIdentifier);
        ItemStackRenderState itemRenderState = new ItemStackRenderState();

        model.update(
            itemRenderState,
            state.needleSword,
            client.getItemModelResolver(),
            ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            (ClientLevel) state.world,
            null,
            state.world == null ? 0 : state.world.getRandom().nextInt()
        );

        itemRenderState.submit(
            poseStack,
            submitNodeCollector,
            state.lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        );

        poseStack.popPose();
        poseStack.pushPose();
        if (state.ownerPos != null && state.shouldDisplayString) {
            Vec3 statePos = new Vec3(state.x, state.y, state.z);
	        EntityRenderState.LeashState leashState = new EntityRenderState.LeashState();
            leashState.offset = state.ownerPos.subtract(statePos);
            leashState.start = state.ownerPos;
            leashState.end = statePos;
            leashState.slack = false;
            submitNodeCollector.submitLeash(poseStack, leashState);
        }
        poseStack.popPose();
    }

    @Override
    public void extractRenderState(NeedleSwordEntity entity, NeedleSwordRenderState state, float tickProgress) {
        super.extractRenderState(entity, state, tickProgress);
        state.needleSword = entity.getSword().transmuteCopy(ModItems.NEEDLE_HEAD, 1);
        state.ownerPos = Optional.ofNullable(entity.getOwner()).map(owner -> owner.getRopeHoldPosition(tickProgress)).orElse(null);
        state.world = entity.level();
        state.yRot = entity.getYRot();
        state.xRot = entity.getXRot();
        state.shouldDisplayString = entity.shouldReturn();
    }

    public static class NeedleSwordRenderState extends ArrowRenderState {
        public ItemStack needleSword;
        public Level world;
        public boolean shouldDisplayString;
        public Vec3 ownerPos;
    }
}
