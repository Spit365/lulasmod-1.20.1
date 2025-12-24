package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.packet.BleedProgressS2CPacket;
import net.spit365.lulasmod.packet.SummonBleedS2CPacket;

import java.util.stream.StreamSupport;

public class Bleed {
    public static final Identifier BACKGROUND = Identifier.of(Lulasmod.MOD_ID, "textures/gui/bleed_progress/background.png");
    public static final Identifier PROGRESS = Identifier.of(Lulasmod.MOD_ID, "textures/gui/bleed_progress/progress.png");
    public static int progress = 0;

    public static void tick(ServerWorld world){
        StreamSupport.stream(world.iterateEntities().spliterator(), false).<LivingEntity>mapMulti((target, result) -> {
            if (target instanceof LivingEntity livingEntity && target.isAlive()) result.accept(livingEntity);
        }).forEach(target -> {
            Integer duration = target.getAttached(ModData.BLEED_VALUE);
            if (duration == null) return;
            int threshold = getThreshold(target);
            if (threshold <= 0) return;
            if (duration >= threshold) {
                target.damage(world, ModDamageSources.bloodsucking(world), (target.getMaxHealth() * 0.15f + 10f) * duration / threshold);
                for (ServerPlayerEntity player : world.getPlayers()) {
                    if (player.squaredDistanceTo(target) > 1000000) continue;
                    ServerPlayNetworking.send(player, new SummonBleedS2CPacket(target.getX(), target.getY(), target.getZ()));
                }
            }
            duration = duration % threshold - 1;
            if (duration > 0) target.setAttached(ModData.BLEED_VALUE, duration);
            else target.removeAttached(ModData.BLEED_VALUE);
        });
    }

    public static void tick(ServerPlayerEntity player){
        Integer duration = player.getAttached(ModData.BLEED_VALUE);
        ServerPlayNetworking.send(player, new BleedProgressS2CPacket(duration != null ? duration * 100 / getThreshold(player) : 0));
    }

    private static int getThreshold(LivingEntity entity) {
        return MathHelper.clamp((int) entity.getHealth() * 60, 1, 1200) ;
    }

    public static void apply(LivingEntity entity, int duration){
        Integer bleed = entity.getAttached(ModData.BLEED_VALUE);
        entity.setAttached(ModData.BLEED_VALUE, duration + (bleed != null? bleed : 0));
	}

	public static void summonParticles(Vec3d pos, ClientWorld world) {
		if (world != null) for (int i = 0; i < world.random.nextInt(4) + 6; i++) {
            world.addParticleClient(ModParticles.getBlood(), pos.getX(), pos.getY() + 1, pos.getZ(), 1, 0, 1);
        }
	}

    public static void render(DrawContext drawContext) {
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
