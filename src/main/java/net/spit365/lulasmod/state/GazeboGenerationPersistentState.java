package net.spit365.lulasmod.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GazeboGenerationPersistentState extends PersistentState {
	private static final int count = (int) ModSpells.SpellTabItems.stream().filter(identifier -> !(Registries.ITEM.get(identifier) instanceof ConjuringItem)).count();
	private Map<BlockPos, Boolean> pendingPos;
	public static int RADIUS_BLOCKS = 1000;


	public static final Codec<GazeboGenerationPersistentState> CODEC = RecordCodecBuilder.<Map.Entry<BlockPos, Boolean>>create(instance ->
		instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(Map.Entry::getKey),
			Codec.BOOL.fieldOf("loaded").forGetter(Map.Entry::getValue)
		).apply(instance, AbstractMap.SimpleEntry::new)
	).listOf().xmap(list -> {
		GazeboGenerationPersistentState storage = new GazeboGenerationPersistentState();
		storage.pendingPos.entrySet().addAll(list);
		return storage;
	}, storage -> new ArrayList<>(storage.pendingPos.entrySet()));

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

	public Map<BlockPos, Boolean> getPending(ServerWorld world) {
        if (pendingPos == null) {
            pendingPos = IntStream.range(1, count).mapToObj(value -> {
                double angle = (Math.PI * 2) * ((double) value / count);
                return new BlockPos(
                    (int) Math.round(Math.cos(angle) * RADIUS_BLOCKS),
                    0,
                    (int) Math.round(Math.sin(angle) * RADIUS_BLOCKS)
                );
            }).collect(Collectors.toMap(Function.identity(), o -> false));
        }
        for (Map.Entry<BlockPos, Boolean> entry : new HashSet<>(pendingPos.entrySet())){
            if (entry.getValue()) continue;
            BlockPos pos = entry.getKey();
            if (world.getChunkManager().isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
                pendingPos.remove(pos);
                pendingPos.put(world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos), false);
            }
        }
		return pendingPos;
	}

	public void markAsLoaded(BlockPos key){
		pendingPos.put(key, true);
        this.markDirty();
	}
}
