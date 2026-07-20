package net.spit365.lulasmod.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public record MultiVec3d(Vec3... vec3ds) {
    public static final int MULTI_VEC_DETAIL = 16;

	public Vec3 get(int index) {
		return this.vec3ds[index];
	}

	public Stream<Vec3> stream() {
		return this.pairwiseSegments().flatMap(twoVec3d -> {
			int detail = Math.max(1, (int) (twoVec3d.start().distanceTo(twoVec3d.end()) * MULTI_VEC_DETAIL));
			return IntStream.range(0, detail).mapToObj(j -> Mth.lerp((double) j / detail, twoVec3d.start(), twoVec3d.end()));
		});
	}

	public record TwoVec3d(Vec3 start, Vec3 end) {}
	public Stream<TwoVec3d> pairwiseSegments() {
		return IntStream.range(1, this.vec3ds.length).mapToObj(i -> new TwoVec3d(this.get(i -1), this.get(i)));
	}

	public static final Codec<MultiVec3d> CODEC =
		RecordCodecBuilder.create(instance -> instance.group(
			Vec3.CODEC.listOf().fieldOf("vec3ds").forGetter(multiVec3d -> List.of(multiVec3d.vec3ds))
		).apply(instance, vec3dList -> new MultiVec3d(vec3dList.toArray(Vec3[]::new))));
}
