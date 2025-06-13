package net.spit365.lulasmod.custom.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Server;

public class FancyProjectileEntityRenderer<T extends PersistentProjectileEntity> extends ProjectileEntityRenderer<T> {
    public final Identifier texture;
    public FancyProjectileEntityRenderer(EntityRendererFactory.Context context, String texture) {
        super(context);
        this.texture = new Identifier(Server.MOD_ID, "textures/entity/" + texture);
    }
    @Override public Identifier getTexture(T entity) {return texture;}
}