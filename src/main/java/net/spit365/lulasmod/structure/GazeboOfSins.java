package net.spit365.lulasmod.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
    public static final ResourceLocation GAZEBO_OF_SINS_ID = ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, "gazebo_of_sins");

    public static void tick(ServerLevel world) {
        GazeboGenerationPersistentState gazeboGenerationPersistentState = GazeboGenerationPersistentState.get(world);
        if (gazeboGenerationPersistentState == null) return;
        HashSet<Map.Entry<BlockPos, Boolean>> gazeboPos = new HashSet<>(gazeboGenerationPersistentState.getPending(world).entrySet());

        for (Map.Entry<BlockPos, Boolean> entry : gazeboPos) {
            BlockPos pos = entry.getKey();
            if (entry.getValue()) continue;
            Optional<StructureTemplate> structureTemplate = world.getStructureManager().get(GAZEBO_OF_SINS_ID);
            if (structureTemplate.isEmpty()) continue;

            StructureTemplate gazebo = structureTemplate.get();
            boolean loaded = ChunkPos.rangeClosed(new ChunkPos(pos), new ChunkPos(pos.offset(gazebo.getSize())))
                .allMatch(chunkPos -> world.isLoaded(chunkPos.getWorldPosition()));
            boolean placed = gazebo.placeInWorld(
                world,
                pos,
                pos,
                new StructurePlaceSettings(),
                world.getRandom(),
                Block.UPDATE_CLIENTS
            );
            if (loaded && placed) gazeboGenerationPersistentState.markAsLoaded(pos);
        }
    }
}