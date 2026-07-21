package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModDamageTypes;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.packet.BleedProgressS2CPacket;
import net.spit365.lulasmod.packet.SummonBleedS2CPacket;

import java.util.HashSet;
import java.util.Set;

public final class Bleed {
    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "textures/gui/bleed_progress/background.png");
    public static final Identifier PROGRESS = Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "textures/gui/bleed_progress/progress.png");
    public static int progress = 0;

    public static void tick(ServerLevel world) {
        Set<Vec3> occurrences = new HashSet<>();
        for (Entity entity : world.getAllEntities()) {
            Integer duration = entity.getAttached(ModData.BLEED_VALUE);
            if (entity instanceof ServerPlayer player)
                ServerPlayNetworking.send(player, new BleedProgressS2CPacket(duration != null ? duration * 100 / getThreshold(player) : 0));
            if (duration == null) continue;
            if (!(entity instanceof LivingEntity target) || !target.isAlive() || duration <= 0) {
                entity.removeAttached(ModData.BLEED_VALUE);
                continue;
            }
            int threshold = getThreshold(target);
            if (duration >= threshold) {
                int times = duration / threshold;
                target.hurtServer(world, ModDamageTypes.createDamageSource(world, ModDamageTypes.BLOODSUCKING), (target.getMaxHealth() * 0.15f + 10f) * times);
                duration %= threshold;
                occurrences.add(target.position());
            }
            duration--;
            if (duration > 0) target.setAttached(ModData.BLEED_VALUE, duration);
            else target.removeAttached(ModData.BLEED_VALUE);
        }
        for (ServerPlayer player : world.players()) for (Vec3 pos : occurrences) {
            if (player.distanceToSqr(pos) <= 1_000_000)
                ServerPlayNetworking.send(player, new SummonBleedS2CPacket(pos));
        }
    }

    private static int getThreshold(LivingEntity entity) {
        return Mth.clamp((int) entity.getHealth() * 60, 1, 1200) ;
    }

    public static void apply(LivingEntity entity, int duration) {
        Integer bleed = entity.getAttached(ModData.BLEED_VALUE);
        entity.setAttached(ModData.BLEED_VALUE, duration + (bleed != null? bleed : 0));
	}

	public static void summonParticles(Vec3 pos, ClientLevel world) {
		if (world != null) for (int i = 0; i < world.getRandom().nextInt(4) + 6; i++) {
            world.addParticle(ModParticles.getBlood(), pos.x(), pos.y() + 1, pos.z(), 1, 0, 1);
        }
	}

    public static void render(GuiGraphicsExtractor drawContext) {
        int textWidth = 182;
        int textHeight = 5;
        int x = (drawContext.guiWidth() - textWidth) / 2;
        int y = drawContext.guiHeight() * 3 / 4;
        if (progress > 0) {
            progress = Math.min(progress, 100);
            int l = (int) (progress * 1.83F);
            drawContext.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0, 0, textWidth, textHeight, textWidth, textHeight);
            if (l > 0) {
                drawContext.blit(RenderPipelines.GUI_TEXTURED, PROGRESS, x, y, 0, 0, l, textHeight, textWidth, textHeight);
            }
        }
    }
}
