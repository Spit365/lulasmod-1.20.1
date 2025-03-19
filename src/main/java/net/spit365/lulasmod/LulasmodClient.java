package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.spit365.lulasmod.custom.HellishSealItem;
import net.spit365.lulasmod.custom.SpellManager;
import net.spit365.lulasmod.mod.ModKeybinds;

import java.util.List;

public class LulasmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this::onHudRender);
        ModKeybinds.init();
    }

    private void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        ItemStack mainHandStack = player.getMainHandStack();

        // Check if holding a Seal
        if (mainHandStack.getItem() instanceof HellishSealItem) {
            List<ItemStack> spells = SpellManager.getSpells(player);
            if (!spells.isEmpty()) {
                int screenWidth = client.getWindow().getScaledWidth();
                int screenHeight = client.getWindow().getScaledHeight();
                int hotbarSlotX = screenWidth / 2 - 88 + (player.getInventory().selectedSlot * 20);
                int hotbarSlotY = screenHeight - 22;

                // Render spell slots
                for (int i = 0; i < Math.min(spells.size(), 3); i++) {
                    context.drawItem(spells.get(i), hotbarSlotX, hotbarSlotY - 60 + (i * 20));
                }
            }
        }
    }
}
