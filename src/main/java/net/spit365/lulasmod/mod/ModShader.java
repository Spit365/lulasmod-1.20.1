package net.spit365.lulasmod.mod;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mixin.GameRendererAccessor;
import net.spit365.lulasmod.mixin.PostEffectPassAccessor;
import net.spit365.lulasmod.mixin.PostEffectProcessorAccessor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

public class ModShader {

    public static final Identifier BLACK_HOLE = Identifier.of(Lulasmod.MOD_ID, "bw");

    public static void init(){
        WorldRenderEvents.END.register(worldRenderContext -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PostEffectProcessor blackHole = client.getShaderLoader().loadPostEffect(BLACK_HOLE, DefaultFramebufferSet.MAIN_ONLY);
            if (blackHole != null) {
                Framebuffer framebuffer = client.getFramebuffer();
                FrameGraphBuilder builder = new FrameGraphBuilder();
				//setBwStrength(blackHole, 0.5f);
                blackHole.render(builder, framebuffer.textureWidth, framebuffer.textureHeight, PostEffectProcessor.FramebufferSet.singleton(
                    PostEffectProcessor.MAIN,
                    builder.createObjectNode("main", framebuffer)
                ));
                builder.run(((GameRendererAccessor) client.gameRenderer).pool());
            }
        });
    }
	public static void setBwStrength(PostEffectProcessor processor, float strength) {
        List<PostEffectPass> passes =
                ((PostEffectProcessorAccessor) processor).passes();
        if (passes.isEmpty()) return;
        PostEffectPass pass = passes.getFirst();
        Map<String, GpuBuffer> uniformBuffers = ((PostEffectPassAccessor) pass).uniformBuffers();
        GpuBuffer buffer = uniformBuffers.get("BwConfig");
        if (buffer == null) return;

        GpuBufferSlice slice = buffer.slice(0, 16);


        ByteBuffer data = ByteBuffer
                .allocateDirect(slice.length())
                .order(ByteOrder.nativeOrder());
        data.putFloat(0, strength);
        data.rewind();

        CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
        encoder.writeToBuffer(slice, data);
    }
}
