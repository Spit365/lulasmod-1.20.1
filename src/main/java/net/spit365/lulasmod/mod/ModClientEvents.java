package net.spit365.lulasmod.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.custom.SpellHotbar;
import org.lwjgl.glfw.GLFW;
import java.util.LinkedList;
import java.util.stream.StreamSupport;
import static net.spit365.lulasmod.custom.entity.renderer.TailFeatureRenderer.TAILED_PLAYER_LIST;

@Environment(EnvType.CLIENT)
public class ModClientEvents {
    private static int forwardCounter = 2400;
    private static long timeOfDay = 0;
    private static final LinkedList<ItemStack> SPELL_HOTBAR_LIST = new LinkedList<>();
    private static final Identifier SPELL_HOTBAR_TEXTURE = Identifier.of(Server.MOD_ID, "textures/gui/spell_hotbar.png");
    private static final KeyBinding CYCLE_SPELL_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.lulasmod.cycle_spell",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "key.categories.lulasmod"
    ));

    public static void init() {
        HudRenderCallback.EVENT.register((context, v) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (StreamSupport.stream(player.getHandItems().spliterator(), false).anyMatch(stack -> stack.getItem() instanceof SpellHotbar)){
                RenderSystem.enableBlend();
                int x =  context.getScaledWindowWidth() / 2 - 121;
                int y = context.getScaledWindowHeight() - 42;
                if (player.getMainHandStack().getItem() instanceof SpellHotbar)
                    x += (player.getInventory().selectedSlot * 20) + 29;
                context.drawTexture(SPELL_HOTBAR_TEXTURE, x, y -44, 0, 0,24, 64, 24, 64);
                for (int i = 0; i < Math.min(SPELL_HOTBAR_LIST.size(), 3); i++) context.drawItem(SPELL_HOTBAR_LIST.get(i), x +4, y + (i * -20));
                RenderSystem.disableBlend();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CYCLE_SPELL_KEY.wasPressed() && client.player != null) ClientPlayNetworking.send(new ModPackets.CYCLE_PLAYER_SPELL_C2S());
            ClientWorld world = client.world;
            if (world != null) {
                if (forwardCounter < 300) {
                    forwardCounter += 2;
                    timeOfDay += forwardCounter;
                    world.setTimeOfDay(timeOfDay);
                } else if (forwardCounter < 600) {
                    forwardCounter++;
                    timeOfDay += 1200;
                    world.setTimeOfDay(timeOfDay);
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.SPELL_HOTBAR_LIST_S2C.ID, (payload, context) ->  {
            SPELL_HOTBAR_LIST.clear();
            payload.list().forEach(id -> SPELL_HOTBAR_LIST.add(new ItemStack(Registries.ITEM.get(id))));
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TAILED_PLAYER_LIST_S2C.ID, (payload, context) ->  {
            TAILED_PLAYER_LIST.clear();
            TAILED_PLAYER_LIST.addAll(payload.list());
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.TIME_FORWARD_ANIMATION_S2C.ID, (payload, context) -> {
            forwardCounter = 1;
            ClientWorld world = context.client().world;
            if (world != null) timeOfDay = world.getTimeOfDay();
        });
    }
}