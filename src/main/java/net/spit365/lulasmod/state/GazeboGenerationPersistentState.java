package net.spit365.lulasmod.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GazeboGenerationPersistentState extends SavedData {
	private static final int count = (int) ModSpells.SpellTabItems.stream().filter(stack -> !(stack.get().getItem() instanceof ConjuringItem) && !stack.get().is(ModSpells.HIGHLIGHTER_SPELL)).count();
	private Map<BlockPos, Boolean> pendingPos;
	public static final int RADIUS_BLOCKS = 1000;


	public static final Codec<GazeboGenerationPersistentState> CODEC = RecordCodecBuilder.<Map.Entry<BlockPos, Boolean>>create(instance ->
		instance.group(
			BlockPos.CODEC.fieldOf("pos").forGetter(Map.Entry::getKey),
			Codec.BOOL.fieldOf("loaded").forGetter(Map.Entry::getValue)
		).apply(instance, AbstractMap.SimpleEntry::new)
	).listOf().xmap(list -> {
		GazeboGenerationPersistentState storage = new GazeboGenerationPersistentState();
        if (storage.pendingPos != null)	storage.pendingPos.entrySet().addAll(list);
		return storage;
	}, storage -> new ArrayList<>(storage.pendingPos.entrySet()));

	public static final SavedDataType<GazeboGenerationPersistentState> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "gazebo_generation"),
		GazeboGenerationPersistentState::new,
		CODEC,
		DataFixTypes.LEVEL
	);

	public static GazeboGenerationPersistentState get(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(TYPE);
	}

	public Map<BlockPos, Boolean> getPending(ServerLevel world) {
        if (pendingPos == null) {
            pendingPos = IntStream.range(0, count).mapToObj(value -> {
                double angle = (Math.PI * 2) * ((double) value / count);
                return new BlockPos(
                    (int) Math.round(Math.cos(angle) * RADIUS_BLOCKS),
                    0,
                    (int) Math.round(Math.sin(angle) * RADIUS_BLOCKS)
                );
            }).collect(Collectors.toMap(Function.identity(), o -> false));
			Lulasmod.LOGGER.info(pendingPos.keySet().toString());
        }
        for (Map.Entry<BlockPos, Boolean> entry : new HashSet<>(pendingPos.entrySet())) {
            if (entry.getValue()) continue;
            BlockPos pos = entry.getKey();
            if (world.getChunkSource().hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
                pendingPos.remove(pos);
                pendingPos.put(world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos), false);
            }
        }
		return pendingPos;
	}

	public void markAsLoaded(BlockPos key) {
		pendingPos.put(key, true);
        this.setDirty();
	}
}
