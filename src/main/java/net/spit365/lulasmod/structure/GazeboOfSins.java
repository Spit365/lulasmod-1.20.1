package net.spit365.lulasmod.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.state.GazeboGenerationPersistentState;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public final class GazeboOfSins {
    public static final Identifier GAZEBO_OF_SINS_ID = Identifier.fromNamespaceAndPath(Lulasmod.MOD_ID, "gazebo_of_sins");

    public static void tick(ServerLevel world) {
        GazeboGenerationPersistentState gazeboGenerationPersistentState = GazeboGenerationPersistentState.get(world);
	    HashSet<Map.Entry<BlockPos, Boolean>> gazeboPos = new HashSet<>(gazeboGenerationPersistentState.getPending(world).entrySet());

        for (Map.Entry<BlockPos, Boolean> entry : gazeboPos) {
            BlockPos start = entry.getKey();
            if (entry.getValue()) continue;
            Optional<StructureTemplate> structureTemplate = world.getStructureManager().get(GAZEBO_OF_SINS_ID);
            if (structureTemplate.isEmpty()) continue;

            StructureTemplate gazebo = structureTemplate.get();
            BlockPos end = start.offset(gazebo.getSize());
            boolean loaded = ChunkPos.rangeClosed(new ChunkPos(start.getX(), start.getZ()), new ChunkPos(end.getX(), end.getZ()))
                .allMatch(chunkPos -> world.isLoaded(chunkPos.getWorldPosition()));
            boolean placed = gazebo.placeInWorld(
                world,
                start,
                start,
                new StructurePlaceSettings(),
                world.getRandom(),
                Block.UPDATE_CLIENTS
            );
            if (loaded && placed) gazeboGenerationPersistentState.markAsLoaded(start);
        }
    }
}