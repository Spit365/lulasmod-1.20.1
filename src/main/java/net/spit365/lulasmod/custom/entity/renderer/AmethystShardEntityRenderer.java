package net.spit365.lulasmod.custom.entity.renderer;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.AmethystShardEntity;

public class AmethystShardEntityRenderer extends FancyProjectileEntityRenderer<AmethystShardEntity> {
    public AmethystShardEntityRenderer(EntityRendererFactory.Context context) {super(context, "amethyst_shard.png");}
}