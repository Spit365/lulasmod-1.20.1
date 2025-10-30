//package net.spit365.lulasmod.mixin;
//
//import net.minecraft.client.color.block.BlockColors;
//import net.minecraft.client.render.model.ModelLoader;
//import net.minecraft.client.render.model.UnbakedModel;
//import net.minecraft.client.util.ModelIdentifier;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.profiler.Profiler;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import java.util.Map;
//
//@Mixin(ModelLoader.class)
//public abstract class ModelLoaderMixin {
//
//    @Shadow protected abstract void add(ModelIdentifier id, UnbakedModel model);
//
//    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
//    private void addGoldenTrident(BlockColors blockColors, Profiler profiler, Map jsonUnbakedModels, Map blockStates, CallbackInfo ci) {
//        this.loadItemModel();
//    }
//
//    @Unique
//    private void loadItemModel(ModelIdentifier id) {
//        Identifier identifier = id.id().withPrefixedPath("item/");
//        UnbakedModel unbakedModel = this.getOrLoadModel(identifier);
//        this.add(id, unbakedModel);
//    }
//}