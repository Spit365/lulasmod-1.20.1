package net.spit365.lulasmod.structure;

import net.minecraft.block.Block;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.state.GazeboGenerationPersistentState;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

public final class GazeboOfSins {
    public static final Identifier GAZEBO_OF_SINS_ID = Identifier.of(Lulasmod.MOD_ID, "gazebo_of_sins");

    public static void tick(ServerWorld world) {
        GazeboGenerationPersistentState gazeboGenerationPersistentState = GazeboGenerationPersistentState.get(world);
        if (gazeboGenerationPersistentState == null) return;
        HashSet<Map.Entry<BlockPos, Boolean>> gazeboPos = new HashSet<>(gazeboGenerationPersistentState.getPending(world).entrySet());

        for (Map.Entry<BlockPos, Boolean> entry : gazeboPos) {
            BlockPos pos = entry.getKey();
            if (entry.getValue()) continue;
            Optional<StructureTemplate> structureTemplate = world.getStructureTemplateManager().getTemplate(GAZEBO_OF_SINS_ID);
            if (structureTemplate.isEmpty()) continue;

            StructureTemplate gazebo = structureTemplate.get();
            boolean loaded = ChunkPos.stream(new ChunkPos(pos), new ChunkPos(pos.add(gazebo.getSize())))
                .allMatch(chunkPos -> world.isChunkLoaded(chunkPos.toLong()));
            boolean placed = gazebo.place(
                world,
                pos,
                pos,
                new StructurePlacementData(),
                world.getRandom(),
                Block.NOTIFY_LISTENERS
            );
            if (loaded && placed) gazeboGenerationPersistentState.markAsLoaded(pos);
        }
    }
}