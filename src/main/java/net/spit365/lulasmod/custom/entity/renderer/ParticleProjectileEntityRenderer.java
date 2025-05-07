package net.spit365.lulasmod.custom.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;

public class ParticleProjectileEntityRenderer extends ProjectileEntityRenderer<ParticleProjectileEntity> {
    private final Identifier texture = new Identifier(Lulasmod.MOD_ID, "textures/entity/projectile.png");
    public ParticleProjectileEntityRenderer(EntityRendererFactory.Context context) {super(context);}

    @Override public Identifier getTexture(ParticleProjectileEntity entity) {return texture;}
}
