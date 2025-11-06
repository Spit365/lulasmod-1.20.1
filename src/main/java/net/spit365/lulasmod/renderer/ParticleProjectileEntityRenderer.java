package net.spit365.lulasmod.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.spit365.lulasmod.entity.ParticleProjectileEntity;

public class ParticleProjectileEntityRenderer extends FancyProjectileEntityRenderer<ParticleProjectileEntity> {
    public ParticleProjectileEntityRenderer(EntityRendererFactory.Context context) {super(context, "projectile.png");}
}
