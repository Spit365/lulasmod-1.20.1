package net.spit365.lulasmod.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.mod.ModServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @SuppressWarnings("AmbiguousMixinReference")
    @ModifyVariable(method = "renderItem", at = @At(value = "HEAD"), argsOnly = true)
    public BakedModel useGoldenTridentModel(BakedModel value, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (stack.isOf(ModServer.Items.GOLDEN_TRIDENT) && renderMode != ModelTransformationMode.GUI) {
            return ((ItemRendererAccessor) this).lulasmod$getModels().getModelManager().getModel(new ModelIdentifier(Server.MOD_ID, "golden_trident_in_hand", "inventory"));
        }
        return value;
    }
}