package net.spit365.lulasmod.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public class FancyProjectileEntityRenderer<T extends PersistentProjectileEntity> extends ProjectileEntityRenderer<T, ProjectileEntityRenderState> {
    public final Identifier texture;
    public FancyProjectileEntityRenderer(EntityRendererFactory.Context context, String texture) {
        super(context);
        this.texture = Identifier.of(Lulasmod.MOD_ID, "textures/entity/" + texture);
    }
    @Override protected Identifier getTexture(ProjectileEntityRenderState state) {return texture;}

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }
}