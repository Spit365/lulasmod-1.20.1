package net.spit365.lulasmod.structure;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.state.GazeboGenerationPersistentState;

import java.util.*;

public final class GazeboOfSins {
    public static final Identifier GAZEBO_OF_SINS_ID = Identifier.of(Lulasmod.MOD_ID, "gazebo_of_sins");

	public static void tick(ServerWorld serverWorld){
		GazeboGenerationPersistentState gazeboGenerationPersistentState = GazeboGenerationPersistentState.get(serverWorld);
		if (gazeboGenerationPersistentState == null) return;
        HashSet<Map.Entry<BlockPos, Boolean>> gazeboPos = new HashSet<>(gazeboGenerationPersistentState.getPending(serverWorld).entrySet());
        for (Map.Entry<BlockPos, Boolean> entry : gazeboPos){
            if (!entry.getValue()) return;
            BlockPos pos = entry.getKey();
            ChunkPos chunkPos = new ChunkPos(pos);
            if (!serverWorld.getChunkManager().isChunkLoaded(chunkPos.x, chunkPos.z)) continue;
            serverWorld.getStructureTemplateManager().getTemplate(GAZEBO_OF_SINS_ID).ifPresent(structureTemplate -> structureTemplate.place(
                serverWorld,
                pos,
                pos,
                new StructurePlacementData(),
                serverWorld.getRandom(),
                Block.FORCE_STATE_AND_SKIP_CALLBACKS_AND_DROPS
            ));
            gazeboGenerationPersistentState.markAsLoaded(pos);
        }
	}
}