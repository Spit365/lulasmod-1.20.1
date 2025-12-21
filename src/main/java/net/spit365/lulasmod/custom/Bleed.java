package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.packet.BleedProgressS2CPacket;
import net.spit365.lulasmod.packet.SummonBleedS2CPacket;
import org.jetbrains.annotations.Nullable;

import java.util.stream.StreamSupport;

public class Bleed {
    public static final Identifier BACKGROUND = Identifier.of(Lulasmod.MOD_ID, "textures/gui/bleed_progress/background.png");
    public static final Identifier PROGRESS = Identifier.of(Lulasmod.MOD_ID, "textures/gui/bleed_progress/progress.png");
    public static int progress = 0;

    public static void tick(ServerWorld world){
        StreamSupport.stream(world.iterateEntities().spliterator(), true).filter(entity -> entity instanceof LivingEntity && entity.getAttached(ModData.BLEED_VALUE) != null).map(LivingEntity.class::cast).forEach(entity -> {
            Integer duration = getDuration(entity);
            assert duration != null;
            int threshold = getThreshold(entity);
            if (duration > threshold) {
                duration -= threshold;
                entity.damage(world, ModDamageSources.bloodsucking(world), entity.getMaxHealth() * 0.15f + 10f);
                StreamSupport.stream(world.iterateEntities().spliterator(), true)
				    .filter(target -> target.squaredDistanceTo(entity) < 1000000 /*1000²*/ && target instanceof ServerPlayerEntity)
					.forEach(player -> ServerPlayNetworking.send((ServerPlayerEntity) player, new SummonBleedS2CPacket(entity.getX(), entity.getY(), entity.getZ())));
            }
            duration--;
            entity.setAttached(ModData.BLEED_VALUE, duration);
        });
    }

    private static int getThreshold(LivingEntity entity) {
        return Math.min((int) (Math.min(entity.getHealth(), entity.getMaxHealth()) * 60), 1200);
    }

    private static @Nullable Integer getDuration(LivingEntity entity) {
        return entity.getAttached(ModData.BLEED_VALUE);
    }

    public static void tick(ServerPlayerEntity player){
        Integer duration = getDuration(player);
        if (duration != null)
            ServerPlayNetworking.send(player, new BleedProgressS2CPacket(duration * 100 / Math.max(getThreshold(player), 1)));
    }

    public static void apply(LivingEntity entity, int duration){
        Integer bleed = getDuration(entity);
        entity.setAttached(ModData.BLEED_VALUE, duration + (bleed != null? bleed : 0));
	}

	public static void summonParticles(Vec3d pos, ClientWorld world) {
		if (world != null) for (int i = 0; i < world.random.nextInt(4) + 6; i++) {
             assert ModParticles.BLOOD != null;
            world.addParticleClient(ModParticles.BLOOD, pos.getX(), pos.getY() + 1, pos.getZ(), 1, 0, 1);
        }
	}

    public static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        int textWidth = 182;
        int textHeight = 5;
        int x = (drawContext.getScaledWindowWidth() - textWidth) / 2;
        int y = drawContext.getScaledWindowHeight() * 3 / 4;
        if (progress > 0) {
            progress = Math.min(progress, 100);
            int l = (int) (progress * 1.83F);
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, textWidth, textHeight, textWidth, textHeight);
            if (l > 0) {
                drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, PROGRESS, x, y, 0, 0, l, textHeight, textWidth, textHeight);
            }
        }
    }
}
