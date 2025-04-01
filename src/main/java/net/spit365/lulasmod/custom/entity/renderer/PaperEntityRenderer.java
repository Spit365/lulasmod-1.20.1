package net.spit365.lulasmod.custom.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.entity.PaperEntity;

@Environment(EnvType.CLIENT)
public class PaperEntityRenderer extends ProjectileEntityRenderer<PaperEntity> {
	public static final Identifier TEXTURE = new Identifier("textures/item/paper.png");

	public PaperEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public Identifier getTexture(PaperEntity paperEntity) {
		return TEXTURE;
	}
}