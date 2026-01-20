package net.spit365.lulasmod.renderer;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.util.BoxContext;
import org.joml.Matrix4f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public final class BoxOutlineRenderer {
    private static Set<ColoredEdge> state = new HashSet<>();

    public static void setState(Set<BoxContext> newState) {
        Int2ObjectOpenHashMap<List<Box>> byColor = new Int2ObjectOpenHashMap<>();
        Set<ColoredEdge> result = new HashSet<>();
        for (BoxContext bc : newState) byColor.computeIfAbsent(bc.color(), k -> new ArrayList<>()).add(bc.box());
        for (Int2ObjectMap.Entry<List<Box>> entry : byColor.int2ObjectEntrySet()) {
            int color = entry.getIntKey();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            float a = (color >>> 24) != 0 ? ((color >>> 24) & 0xFF) / 255f : 1.0f;

            for (Calculator.Edge visibleEdge : Calculator.getVisibleEdges(entry.getValue().toArray(Box[]::new))) {
                result.add(new ColoredEdge(visibleEdge, r, g, b, a));
            }
        }
        state = result;
    }

    public static void init() {
        WorldRenderEvents.LAST.register(BoxOutlineRenderer::render);
    }

    private static void render(WorldRenderContext ctx) {
        if (state.isEmpty()) return;

        var camera = ctx.camera();
        Vec3d cam = camera.getPos();

        MatrixStack matrices = ctx.matrixStack();
        if (matrices == null) return;
        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        VertexConsumerProvider vcp = ctx.consumers();
        if (vcp == null) return;
        VertexConsumer vc = vcp.getBuffer(RenderLayer.getLines());
        Matrix4f mat = matrices.peek().getPositionMatrix();

        for (ColoredEdge coloredEdge : state) {
            renderEdge(mat, vc, coloredEdge.edge, coloredEdge.r, coloredEdge.g,  coloredEdge.b, coloredEdge.a);
        }
        matrices.pop();
        if (vcp instanceof VertexConsumerProvider.Immediate immediate) {
            immediate.draw();
        }
    }

    private static void renderEdge(Matrix4f mat, VertexConsumer vc, Calculator.Edge edge, float r, float g, float b, float a) {
        double len = edge.length();
        if (len <= 1e-12) return;
        double dt = 0.25 / len;
        for (double t = 0; t + dt <= 1d + 1e-12; t += dt) {
            Vec3d p0 = edge.at(t);
            Vec3d p1 = edge.at(Math.min(t + dt, 1));
            vc.vertex(mat, (float) p0.x, (float) p0.y, (float) p0.z).color(r, g, b, a).normal(0, 0, 0);
            vc.vertex(mat, (float) p1.x, (float) p1.y, (float) p1.z).color(r, g, b, a).normal(0, 0, 0);
        }
    }

    private record ColoredEdge(Calculator.Edge edge, float r, float g, float b, float a)  {}

    public static class Calculator {
        private static final int[][] DIRS = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};

        private static boolean boxContainsBox(Box box, double x0, double y0, double z0, double x1, double y1, double z1) {
            return box.minX <= x0 && box.maxX >= x1 && box.minY <= y0 && box.maxY >= y1 && box.minZ <= z0 && box.maxZ >= z1;
        }

        private static void addEdge(Set<Edge> set, double x1, double y1, double z1, double x2, double y2, double z2) {
            Vec3d a = new Vec3d(x1, y1, z1);
            Vec3d b = new Vec3d(x2, y2, z2);
            if (compare(a, b) <= 0) set.add(new Edge(a, b));
            else set.add(new Edge(b, a));
        }

        private static int compare(Vec3d a, Vec3d b) {
            if (a.x != b.x) return Double.compare(a.x, b.x);
            if (a.y != b.y) return Double.compare(a.y, b.y);
            return Double.compare(a.z, b.z);
        }

        /**
         * public entry
         **/

        public static Set<Edge> getVisibleEdges(Box... boxes) {
            double[] X = Arrays.stream(boxes).flatMapToDouble(b -> DoubleStream.of(b.minX, b.maxX)).toArray();
            double[] Y = Arrays.stream(boxes).flatMapToDouble(b -> DoubleStream.of(b.minY, b.maxY)).toArray();
            double[] Z = Arrays.stream(boxes).flatMapToDouble(b -> DoubleStream.of(b.minZ, b.maxZ)).toArray();

            int nx = X.length - 1;
            int ny = Y.length - 1;
            int nz = Z.length - 1;
            boolean[][][] solid = new boolean[nx][ny][nz];

            for (int i = 0; i < nx; i++)
                for (int j = 0; j < ny; j++)
                    for (int k = 0; k < nz; k++)
                        for (Box b : boxes)
                            if (boxContainsBox(b, X[i], Y[j], Z[k], X[i + 1], Y[j + 1], Z[k + 1])) {
                                solid[i][j][k] = true;
                                break;
                            }

            Map<PlaneKey, List<Rect>> faces = new HashMap<>();

            for (int i = 0; i < nx; i++)
                for (int j = 0; j < ny; j++)
                    for (int k = 0; k < nz; k++) {
                        if (!solid[i][j][k]) continue;
                        for (int[] d : DIRS) {
                            int ni = i + d[0], nj = j + d[1], nk = k + d[2];
                            if (
                                ni >= 0 &&
                                nj >= 0 &&
                                nk >= 0 &&
                                ni < nx &&
                                nj < ny &&
                                nk < nz &&
                                solid[ni][nj][nk]
                            ) continue;
                            double x0 = X[i], x1 = X[i + 1];
                            double y0 = Y[j], y1 = Y[j + 1];
                            double z0 = Z[k], z1 = Z[k + 1];

                            if (d[0] != 0) faces.computeIfAbsent(new PlaneKey(Direction.Axis.X, d[0] < 0 ? x0 : x1), k2 -> new ArrayList<>()).add(new Rect(y0, z0, y1, z1));
                            else if (d[1] != 0) faces.computeIfAbsent(new PlaneKey(Direction.Axis.Y, d[1] < 0 ? y0 : y1), k2 -> new ArrayList<>()).add(new Rect(x0, z0, x1, z1));
                            else faces.computeIfAbsent(new PlaneKey(Direction.Axis.Z, d[2] < 0 ? z0 : z1), k2 -> new ArrayList<>()).add(new Rect(x0, y0, x1, y1));

                        }
                    }

            Set<Edge> result = new HashSet<>();

            for (Map.Entry<PlaneKey, List<Rect>> entry : faces.entrySet()) {
                PlaneKey key = entry.getKey();
                List<Rect> rects = entry.getValue();

                Set<Segment> outline = computeOutline(rects);

                for (Segment s : outline) {
                    switch (key.axis) {
                        case X -> addEdge(result, key.coord, s.a, s.b, key.coord, s.c, s.d);
                        case Y -> addEdge(result, s.a, key.coord, s.b, s.c, key.coord, s.d);
                        case Z -> addEdge(result, s.a, s.b, key.coord, s.c, s.d, key.coord);
                    }
                }
            }

            return result;
        }

        /**
         * 2D rectangle union → outline
         **/

        private static Set<Segment> computeOutline(List<Rect> rects) {
            Map<Segment, Integer> edges = new HashMap<>();

            for (Rect r : rects) {
                edges.merge(new Segment(r.u0, r.v0, r.u1, r.v0), 1, Integer::sum);
                edges.merge(new Segment(r.u1, r.v0, r.u1, r.v1), 1, Integer::sum);
                edges.merge(new Segment(r.u1, r.v1, r.u0, r.v1), 1, Integer::sum);
                edges.merge(new Segment(r.u0, r.v1, r.u0, r.v0), 1, Integer::sum);
            }

            return edges.entrySet().stream().filter(e -> e.getValue() == 1).map(Map.Entry::getKey).collect(Collectors.toSet());
        }

        private record PlaneKey(Direction.Axis axis, double coord) { }

        private record Rect(double u0, double v0, double u1, double v1) { }

        private record Segment(double a, double b, double c, double d) {
            @Override
            public boolean equals(Object o) {
                if (!(o instanceof Segment(double a1, double b1, double c1, double d1))) return false;
                return (a == a1 && b == b1 && c == c1 && d == d1) || (a == c1 && b == d1 && c == a1 && d == b1);
            }

            @Override
            public int hashCode() {
                return Double.hashCode(a + b + c + d);
            }
        }

        public record Edge(Vec3d a, Vec3d b) {
            Vec3d at(double t) {
                return a.lerp(b, t);
            }

            double length() {
                return a.distanceTo(b);
            }
        }
    }
}