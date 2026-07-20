package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.CommonColors;
import net.spit365.lulasmod.custom.DashSpell;

@Environment(EnvType.CLIENT)
public final class DashSpellRenderer {
    public static void render(GuiGraphics context) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        Integer maxUsages = DashSpell.showUsages(player);
        if (maxUsages == null) return;

        int x = context.guiWidth() / 2 - 5;
        int y = context.guiHeight() / 2 - 3;

        if (DashSpell.usages <= 0)  DashSpell.usages = maxUsages;
        int color = DashSpell.usages != 1 ? CommonColors.WHITE : (player.onGround() ? CommonColors.YELLOW : CommonColors.RED);
        context.drawString(client.font, String.valueOf(DashSpell.usages), x - 11, y, color, true);
        context.drawString(client.font, String.valueOf(maxUsages), x + 15, y, color, true);
    }
}
