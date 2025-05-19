package net.spit365.lulasmod.custom.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;

public class ParticleProjectileEntityRenderer extends FancyProjectileEntityRenderer<ParticleProjectileEntity> {
    public ParticleProjectileEntityRenderer(EntityRendererFactory.Context context) {super(context, "projectile.png");}
}
