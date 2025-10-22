package net.spit365.lulasmod.custom.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GazeboGenerationPersistentState extends PersistentState {
	private static final int count = (int) ModSpells.SpellTabItems.stream().filter(identifier -> !(Registries.ITEM.get(identifier) instanceof ConjuringItem)).count();
	private Map<BlockPos, Long> gazeboPos;
	public static int RADIUS_BLOCKS = 1000;


	public static final Codec<GazeboGenerationPersistentState> CODEC = RecordCodecBuilder.<Map.Entry<BlockPos, Long>>create(instance ->
		instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(Map.Entry::getKey),
			Codec.LONG.fieldOf("chunk").forGetter(Map.Entry::getValue)
		).apply(instance, AbstractMap.SimpleEntry::new)
	).listOf().xmap(list -> {
		GazeboGenerationPersistentState storage = new GazeboGenerationPersistentState();
		storage.gazeboPos.entrySet().addAll(list);
		return storage;
	}, storage -> new ArrayList<>(storage.gazeboPos.entrySet()));

	public static final PersistentStateType<GazeboGenerationPersistentState> TYPE =
		new PersistentStateType<>(
			"gazebo_generation",
			ctx -> new GazeboGenerationPersistentState(),
			ctx -> CODEC,
			DataFixTypes.LEVEL
		);

	public static GazeboGenerationPersistentState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(TYPE);
	}

	public Map<BlockPos, Long> getPos(ServerWorld world) {
		if (gazeboPos == null){
			gazeboPos = IntStream.range(1, count).mapToObj(value -> {
				double angle = (Math.PI * 2) * ((double) value / count);
				return world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, new BlockPos(
					(int) Math.round(Math.cos(angle) * RADIUS_BLOCKS),
					0,
					(int) Math.round(Math.sin(angle) * RADIUS_BLOCKS)
				));
			}).collect(Collectors.toMap(pos -> pos, pos -> world.getChunk(pos).getPos().toLong()));
		}
		return gazeboPos;
	}

	public void remove(BlockPos key){
		gazeboPos.remove(key);
	}
}
