package net.spit365.lulasmod.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.util.SpellHotbar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SpellHotbarRenderer {
    private static final Identifier SPELL_HOTBAR_TEXTURE = Identifier.of(Lulasmod.MOD_ID, "textures/gui/spell_hotbar.png");
    public static List<ItemStack> spellHotbarList = new LinkedList<>();

    public static void render(DrawContext context) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || Arrays.stream(Hand.values()).noneMatch(hand -> player.getStackInHand(hand).getItem() instanceof SpellHotbar)) return;
        int x = context.getScaledWindowWidth() / 2 - 121 + (player.getMainHandStack().getItem() instanceof SpellHotbar ? (player.getInventory().getSelectedSlot() * 20) + 29 : 0);
        int y = context.getScaledWindowHeight() - 42;
        context.drawTexture(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, SPELL_HOTBAR_TEXTURE, x, y - 44, 0, 0, 24, 64, 24, 64);
        for (int i = 0; i < Math.min(spellHotbarList.size(), 3); i++)
            context.drawItem(spellHotbarList.get(i), x + 4, y + (i * -20));
    }
}
