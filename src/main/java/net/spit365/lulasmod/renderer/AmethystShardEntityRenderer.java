package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.entity.AmethystShardEntity;
@Environment(EnvType.CLIENT)
public class AmethystShardEntityRenderer extends ProjectileEntityRenderer<AmethystShardEntity> {
    public static final Identifier TEXTURE = Identifier.of(Lulasmod.MOD_ID, "textures/entity/amethyst_shard.png");

    public AmethystShardEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public Identifier getTexture(AmethystShardEntity entity) {
        return TEXTURE;
    }
}