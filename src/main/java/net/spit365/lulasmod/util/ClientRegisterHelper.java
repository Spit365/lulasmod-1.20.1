package net.spit365.lulasmod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.particle.SimpleParticleType;
import net.spit365.lulasmod.Lulasmod;

public final class ClientRegisterHelper {
	public static <T extends Entity> void entity(EntityType<T> entityType, EntityRendererFactory<T> entityRendererFactory) {
		EntityRendererRegistry.register(entityType, entityRendererFactory);
	}

	public static KeyBinding keyBinding(String name, int key){
		return KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key." + Lulasmod.MOD_ID + "." + name,
			InputUtil.Type.KEYSYM,
			key,
			"key.categories." + Lulasmod.MOD_ID
		));
	}

	public static void particle(SimpleParticleType particle, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> render){
		 ParticleFactoryRegistry.getInstance().register(particle, render);
	}

    public static void hudElement(HudRenderCallback hudElement) {
		HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
			RenderSystem.enableBlend();
			hudElement.onHudRender(drawContext, renderTickCounter);
			RenderSystem.disableBlend();
		});
    }
}
