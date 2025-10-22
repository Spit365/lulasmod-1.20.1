package net.spit365.lulasmod.custom.structures;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.state.GazeboGenerationPersistentState;

import java.util.*;

public final class GazeboOfSins {
    public static final Identifier GAZEBO_OF_SINS_ID = Identifier.of(Lulasmod.MOD_ID, "gazebo_of_sins");

	public static void place(ServerWorld serverWorld){
		GazeboGenerationPersistentState gazeboGenerationPersistentState = GazeboGenerationPersistentState.get(serverWorld);
		if (gazeboGenerationPersistentState != null) {
			Set<Map.Entry<BlockPos, Long>> gazeboPos = gazeboGenerationPersistentState.getPos(serverWorld).entrySet();
			for (Map.Entry<BlockPos, Long> entry : gazeboPos){
				if (serverWorld.isChunkLoaded(entry.getValue())){
					BlockPos pos = entry.getKey();
					Lulasmod.LOGGER.info(String.valueOf(pos));
//					Optional<StructureTemplate> optional = serverWorld.getStructureTemplateManager().getTemplate(GAZEBO_OF_SINS_ID);
//					optional.ifPresent(structureTemplate -> structureTemplate.place(
//						serverWorld,
//						pos,
//						pos,
//						new StructurePlacementData(),
//						serverWorld.getRandom(),
//						Block.FORCE_STATE_AND_SKIP_CALLBACKS_AND_DROPS
//					));
//					gazeboGenerationPersistentState.remove(pos);
				}
			}
		}
	}
}