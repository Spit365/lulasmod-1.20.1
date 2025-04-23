package net.spit365.lulasmod.custom.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.GoldenProjectileEntity;

public class GoldenProjectileEntityRenderer extends ProjectileEntityRenderer<GoldenProjectileEntity> {
    public GoldenProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(GoldenProjectileEntity entity) {
        return new Identifier(Lulasmod.MOD_ID, "textures/entity/golden_projectile.png");
    }
}
