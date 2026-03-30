package net.spit365.lulasmod.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

import java.util.function.Supplier;

public final class ClientRegisterHelper {
	public static <T extends Entity> void entity(EntityType<T> entityType, EntityRendererFactory<T> entityRendererFactory) {
		EntityRendererRegistry.register(entityType, entityRendererFactory);
	}

	public static KeyBinding keyBinding(String name, int key) {
		return KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key." + Lulasmod.MOD_ID + "." + name,
			InputUtil.Type.KEYSYM,
			key,
			"key.categories." + Lulasmod.MOD_ID
		));
	}

	public static KeyBinding packetKeyBinding(String name, int key, boolean repeating, Supplier<CustomPayload> payloadSupplier) {
		KeyBinding keyBinding = keyBinding(name, key);
        ClientTickEvents.END_CLIENT_TICK.register(repeating ?
            client -> {
				if (keyBinding.wasPressed()) ClientPlayNetworking.send(payloadSupplier.get());
			} :
            client -> {
				if (keyBinding.isPressed()) ClientPlayNetworking.send(payloadSupplier.get());
			}
		);
		return keyBinding;
	}

	public static void particle(SimpleParticleType particle, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> render) {
		 ParticleFactoryRegistry.getInstance().register(particle, render);
	}

    public static void hudElement(String name, HudElement hudElement) {
        HudElementRegistry.addLast(Identifier.of(Lulasmod.MOD_ID, name), hudElement);
    }
}
