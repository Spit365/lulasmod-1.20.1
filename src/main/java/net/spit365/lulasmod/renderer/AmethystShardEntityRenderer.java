package net.spit365.lulasmod.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.entity.AmethystShardEntity;

public class AmethystShardEntityRenderer extends ProjectileEntityRenderer<AmethystShardEntity, ProjectileEntityRenderState> {
    public static final Identifier TEXTURE = Identifier.of(Lulasmod.MOD_ID, "textures/entity/amethyst_shard.png");

    public AmethystShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }
    @Override protected Identifier getTexture(ProjectileEntityRenderState state) {return TEXTURE;}

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }
}