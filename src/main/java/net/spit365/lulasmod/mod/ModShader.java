package net.spit365.lulasmod.mod;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mixin.GameRendererAccessor;
import net.spit365.lulasmod.mixin.PostEffectPassAccessor;
import net.spit365.lulasmod.mixin.PostEffectProcessorAccessor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ModShader {
    private static final ByteBuffer BW_UNIFORM_BUFFER = ByteBuffer.allocateDirect(16).order(ByteOrder.nativeOrder());
    public static final Identifier BLACK_HOLE_ID = Identifier.of(Lulasmod.MOD_ID, "bw");
    public static PostEffectProcessor blackHole;

    public static void init(){
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleResourceReloadListener<Void>() {
            @Override
            public Identifier getFabricId() {
                return BLACK_HOLE_ID;
            }

            @Override
            public CompletableFuture<Void> load(ResourceManager resourceManager, Executor executor) {
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public CompletableFuture<Void> apply(Void unused, ResourceManager resourceManager, Executor executor) {
                return CompletableFuture.runAsync(() -> {
                    MinecraftClient client = MinecraftClient.getInstance();
                    client.execute(() -> loadBlackHole(client));
                });
            }
        });

        HudElementRegistry.addLast(BLACK_HOLE_ID, (drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            try {
                if (blackHole != null) {
                    setBwStrength(blackHole, 0.5f);
                    Framebuffer framebuffer = client.getFramebuffer();
                    FrameGraphBuilder builder = new FrameGraphBuilder();
                    blackHole.render(builder, framebuffer.textureWidth, framebuffer.textureHeight, PostEffectProcessor.FramebufferSet.singleton(
                        PostEffectProcessor.MAIN,
                        builder.createObjectNode("main", framebuffer)
                    ));
                    try {
                        builder.run(((GameRendererAccessor) client.gameRenderer).pool());
                    } catch (IllegalArgumentException ignored) {
                        loadBlackHole(client);
                    }
                }
            } catch (Exception ignored) {}
        });
    }

    private static void loadBlackHole(MinecraftClient client) {
        blackHole = client.getShaderLoader().loadPostEffect(BLACK_HOLE_ID, DefaultFramebufferSet.MAIN_ONLY);
    }

    public static void setBwStrength(PostEffectProcessor processor, float strength) {
        GpuBuffer buffer = getUniformBuffers(processor).get("BwConfig");
        if (buffer == null || buffer.size() < 16) return;

        BW_UNIFORM_BUFFER.putFloat(0, strength);
        BW_UNIFORM_BUFFER.rewind();

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(buffer.slice(0, 16), BW_UNIFORM_BUFFER);
    }

    private static Map<String, GpuBuffer> getUniformBuffers(PostEffectProcessor processor) {
        List<PostEffectPass> passes = ((PostEffectProcessorAccessor) processor).passes();
        if (passes.isEmpty()) return Map.of();
        return ((PostEffectPassAccessor) passes.getFirst()).uniformBuffers();
    }
}
