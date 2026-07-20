package net.spit365.lulasmod.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.spit365.lulasmod.Lulasmod;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.function.Supplier;

public final class ClientRegisterHelper {
	public static <T extends Entity> void entity(EntityType<T> entityType, EntityRendererProvider<T> entityRendererFactory) {
		EntityRendererRegistry.register(entityType, entityRendererFactory);
	}

	public static KeyMapping keyBinding(String name, int key) {
		return KeyBindingHelper.registerKeyBinding(new KeyMapping(
			"key." + Lulasmod.MOD_ID + "." + name,
			InputConstants.Type.KEYSYM,
			key,
			"key.categories." + Lulasmod.MOD_ID
		));
	}

	public static KeyMapping packetKeyBinding(String name, int key, boolean repeating, Supplier<CustomPacketPayload> payloadSupplier) {
		KeyMapping keyBinding = keyBinding(name, key);
        ClientTickEvents.END_CLIENT_TICK.register(repeating ?
            client -> {
				if (keyBinding.consumeClick()) ClientPlayNetworking.send(payloadSupplier.get());
			} :
            client -> {
				if (keyBinding.isDown()) ClientPlayNetworking.send(payloadSupplier.get());
			}
		);
		return keyBinding;
	}

	public static void particle(SimpleParticleType particle, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> render) {
		 ParticleFactoryRegistry.getInstance().register(particle, render);
	}

    public static void hudElement(String name, HudElement hudElement) {
        HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, name), hudElement);
    }
}
