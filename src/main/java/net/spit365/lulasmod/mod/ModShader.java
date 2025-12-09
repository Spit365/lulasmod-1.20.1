package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.util.Pool;
import net.minecraft.util.Identifier;

import java.util.Objects;

import static net.spit365.lulasmod.Lulasmod.MOD_ID;

public class ModShader {
    public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ShaderLoader shaderLoader = client.getShaderLoader();
            Objects.requireNonNull(shaderLoader
                .loadPostEffect(Identifier.of(MOD_ID, "black_hole"),
                    DefaultFramebufferSet.MAIN_ONLY)).render(client.getFramebuffer(), new Pool(-1));
        });
    }
}
