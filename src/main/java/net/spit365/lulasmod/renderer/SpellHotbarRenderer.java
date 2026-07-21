package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.util.SpellHotbar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class SpellHotbarRenderer {
    private static final Identifier SPELL_HOTBAR_TEXTURE = Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "textures/gui/spell_hotbar.png");
    public static List<ItemStack> spellHotbarList = new LinkedList<>();

    public static void render(GuiGraphicsExtractor context) {
        Player player = Minecraft.getInstance().player;
        if (player == null || Arrays.stream(InteractionHand.values()).noneMatch(hand -> player.getItemInHand(hand).getItem() instanceof SpellHotbar)) return;
        int x = context.guiWidth() / 2 - 121 + (player.getMainHandItem().getItem() instanceof SpellHotbar ? (player.getInventory().getSelectedSlot() * 20) + 29 : 0);
        int y = context.guiHeight() - 42;
        context.blit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA, SPELL_HOTBAR_TEXTURE, x, y - 44, 0, 0, 24, 64, 24, 64);
        for (int i = 0; i < Math.min(spellHotbarList.size(), 3); i++)
            context.item(spellHotbarList.get(i), x + 4, y + (i * -20));
    }
}
