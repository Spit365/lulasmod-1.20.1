package net.spit365.lulasmod.state;

import com.mojang.serialization.Codec;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentState;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GazeboGenerationPersistentState extends PersistentState {
	private static final int count = (int) ModSpells.SpellTabItems.stream()
		.filter(stack -> !(stack.getItem() instanceof ConjuringItem) && !stack.isOf(ModSpells.HIGHLIGHTER_SPELL))
		.count();

	private Map<BlockPos, Boolean> pendingPos = new HashMap<>();
	public static final int RADIUS_BLOCKS = 1000;

	private static final Codec<Map<BlockPos, Boolean>> MAP_CODEC =
		Codec.unboundedMap(BlockPos.CODEC, Codec.BOOL);

	public static final PersistentState.Type<GazeboGenerationPersistentState> TYPE =
		new PersistentState.Type<>(
			GazeboGenerationPersistentState::new,
			GazeboGenerationPersistentState::fromNbt,
			DataFixTypes.LEVEL
		);

	public GazeboGenerationPersistentState() {}

	public static GazeboGenerationPersistentState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		GazeboGenerationPersistentState state = new GazeboGenerationPersistentState();
		if (nbt.contains("pending_pos", NbtElement.COMPOUND_TYPE)) {
			MAP_CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("pending_pos"))
				.resultOrPartial(Lulasmod.LOGGER::error)
				.ifPresent(map -> state.pendingPos = new HashMap<>(map));
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		MAP_CODEC.encodeStart(NbtOps.INSTANCE, this.pendingPos)
			.resultOrPartial(Lulasmod.LOGGER::error)
			.ifPresent(tag -> nbt.put("pending_pos", tag));
		return nbt;
	}

	public static GazeboGenerationPersistentState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(TYPE, "gazebo_generation");
	}

	public Map<BlockPos, Boolean> getPending(ServerWorld world) {
		if (pendingPos == null || pendingPos.isEmpty()) {
			pendingPos = IntStream.range(0, count).mapToObj(value -> {
				double angle = (Math.PI * 2) * ((double) value / count);
				return new BlockPos(
					(int) Math.round(Math.cos(angle) * RADIUS_BLOCKS),
					0,
					(int) Math.round(Math.sin(angle) * RADIUS_BLOCKS)
				);
			}).collect(Collectors.toMap(Function.identity(), o -> false, (a, b) -> a, HashMap::new));

			Lulasmod.LOGGER.info("Initialized Gazebo positions: " + pendingPos.keySet());
		}

		for (Map.Entry<BlockPos, Boolean> entry : new HashSet<>(pendingPos.entrySet())) {
			if (Boolean.TRUE.equals(entry.getValue())) continue;

			BlockPos pos = entry.getKey();
			if (world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
				pendingPos.remove(pos);
				pendingPos.put(world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos), false);
				this.markDirty();
			}
		}
		return pendingPos;
	}

	public void markAsLoaded(BlockPos key) {
		if (pendingPos != null) {
			pendingPos.put(key, true);
			this.markDirty();
		}
	}
}