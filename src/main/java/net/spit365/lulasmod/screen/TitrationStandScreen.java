package net.spit365.lulasmod.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.spit365.lulasmod.Lulasmod;

public class TitrationStandScreen extends AbstractContainerScreen<TitrationStandScreenHandler> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "textures/gui/container/titration_stand.png");

    public TitrationStandScreen(TitrationStandScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            TEXTURE,
            x,
            y,
            0f,
            0f,
            imageWidth,
            imageHeight,
            imageWidth,
            imageHeight
        );
    }

//    @Override
//    public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
//        super.render(context, mouseX, mouseY, deltaTicks);
//        renderTooltip(context, mouseX, mouseY);
//    }
}
