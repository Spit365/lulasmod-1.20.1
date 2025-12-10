package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mixin.GameRendererAccessor;

public class ModShader {

    public static final Identifier BLACK_HOLE = Identifier.of(Lulasmod.MOD_ID, "bw");

    public static void init(){
        WorldRenderEvents.END.register(worldRenderContext -> {
            MinecraftClient client = MinecraftClient.getInstance();
            PostEffectProcessor blackHole = client.getShaderLoader().loadPostEffect(BLACK_HOLE, DefaultFramebufferSet.MAIN_ONLY);
            if (blackHole != null) {
                Framebuffer framebuffer = client.getFramebuffer();
                FrameGraphBuilder builder = new FrameGraphBuilder();
                blackHole.render(builder, framebuffer.textureWidth, framebuffer.textureHeight, PostEffectProcessor.FramebufferSet.singleton(
                    PostEffectProcessor.MAIN,
                    builder.createObjectNode("main", framebuffer)
                ));
                builder.run(((GameRendererAccessor) client.gameRenderer).pool());
            }
        });
    }
}
