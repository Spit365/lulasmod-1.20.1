package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.manager.MultiVec3d;
import net.spit365.lulasmod.manager.TimeForwardAnimator;
import org.joml.Matrix4f;

import static net.spit365.lulasmod.mod.ModMethods.addBillboardQuad;

public class ModClientEvents {
	public static TimeForwardAnimator timeForwardAnimator;
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/creeper/creeper_armor.png");

	public static void init() {
        TimeForwardAnimator.init();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeybinds.CYCLE_SPELL_KEY.wasPressed() && client.player != null) ClientPlayNetworking.send(new ModPackets.CycleSpellHotbarC2SPacket());
            TimeForwardAnimator.tick(client.world);
        });
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientWorld world = client.world;
            if (world == null || ModPackets.linkedLightnings.isEmpty()) return;

            VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
            MatrixStack matrices = context.matrixStack();
            Matrix4f matrix;
            if (matrices != null) matrix = matrices.peek().getPositionMatrix();
            else matrix = null;

            Camera camera = client.gameRenderer.getCamera();
            Vec3d cameraPos = camera.getPos();

            for (MultiVec3d link : ModPackets.linkedLightnings) {
                link.pairwiseSegments().forEach(twoVec3d -> {
                    VertexConsumer vc = immediate.getBuffer(RenderLayer.getEntityTranslucentEmissive(TEXTURE));

                    Vec3d start = twoVec3d.start().subtract(cameraPos);
                    Vec3d end = twoVec3d.end().subtract(cameraPos);
                    Vec3d dir = end.subtract(start);
                    double totalLength = dir.length();

                    int segments = Math.max(1, (int) (totalLength / 4));
                    Vec3d step = dir.multiply(1.0 / segments);
                    float thickness = 1.0f;

                    Vec3d dirNorm = dir.normalize();
                    Vec3d up = new Vec3d(0, 1, 0);
                    Vec3d offset = up.subtract(dirNorm.multiply(up.dotProduct(dirNorm)));
                    if (offset.lengthSquared() < 1e-6) {
                        offset = new Vec3d(1, 0, 0);
                    }
                    offset = offset.normalize().multiply(thickness);

                    for (int j = 0; j < segments; j++) {
                        Vec3d p0 = start.add(step.multiply(j));
                        Vec3d p1 = start.add(step.multiply(j + 1));

                        addBillboardQuad(vc, matrix, p0, p1, offset, j);
                    }
                });
            }
            immediate.draw();
        });
    }
}