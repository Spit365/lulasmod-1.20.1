package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import net.spit365.lulasmod.util.MultiVec3d;
import org.joml.Matrix4f;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class LinkedLightningRender {
    private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
    public static Set<MultiVec3d> linkedLightnings = new HashSet<>();

    public static void init() {
        LevelRenderEvents.AFTER_SOLID_FEATURES.register(LinkedLightningRender::render);
    }

    private static void render(LevelRenderContext context) {
        if (linkedLightnings.isEmpty()) return;

        Camera camera = context.gameRenderer().mainCamera();
        Vec3 cameraPos = camera.position();

        context.submitNodeCollector().submitCustomGeometry(
            context.poseStack(),
            RenderTypes.entityTranslucentEmissive(TEXTURE),
            (matrix, vc) -> {
                for (MultiVec3d link : linkedLightnings)
                    link.pairwiseSegments().forEach(twoVec3d -> {
                        Vec3 start = twoVec3d.start().subtract(cameraPos);
                        Vec3 end = twoVec3d.end().subtract(cameraPos);
                        Vec3 dir = end.subtract(start);

                        int segments = Math.max(1, (int) (dir.length() / 4));
                        Vec3 step = dir.scale(1.0 / segments);

                        Vec3 dirNorm = dir.normalize();
                        Vec3 up = new Vec3(0, 1, 0);
                        Vec3 offset = up.subtract(dirNorm.scale(up.dot(dirNorm)));
                        if (offset.lengthSqr() < 1e-6) {
                            offset = new Vec3(1, 0, 0);
                        }
                        offset = offset.normalize();

                        for (int j = 0; j < segments; j++) {
                            Vec3 p0 = start.add(step.scale(j));
                            Vec3 p1 = start.add(step.scale(j + 1));

                            addBillboardQuad(vc, matrix.pose(), p0, p1, offset, j);
                        }
                    });
            }
        );
    }

    private static void addBillboardQuad(VertexConsumer vc, Matrix4f matrix, Vec3 p0, Vec3 p1, Vec3 offset, int segmentIndex) {
        float u1 = segmentIndex + 1;
        float v0 = 0;
        float v1 = 1;

        Vec3 p0a = p0.add(offset);
        Vec3 p0b = p0.subtract(offset);
        Vec3 p1a = p1.add(offset);
        Vec3 p1b = p1.subtract(offset);

        vc.addVertex(matrix, (float) p0a.x, (float) p0a.y, (float) p0a.z).setColor(255, 255, 255, 255).setUv(segmentIndex, v0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF000F0, 0xF000F0).setNormal(0, 1, 0);
        vc.addVertex(matrix, (float) p0b.x, (float) p0b.y, (float) p0b.z).setColor(255, 255, 255, 255).setUv(segmentIndex, v1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF000F0, 0xF000F0).setNormal(0, 1, 0);
        vc.addVertex(matrix, (float) p1b.x, (float) p1b.y, (float) p1b.z).setColor(255, 255, 255, 255).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF000F0, 0xF000F0).setNormal(0, 1, 0);
        vc.addVertex(matrix, (float) p1a.x, (float) p1a.y, (float) p1a.z).setColor(255, 255, 255, 255).setUv(u1, v0).setOverlay(OverlayTexture.NO_OVERLAY).setUv2(0xF000F0, 0xF000F0).setNormal(0, 1, 0);
    }
}
