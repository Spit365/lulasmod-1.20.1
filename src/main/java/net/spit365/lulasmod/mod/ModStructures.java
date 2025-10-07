package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.registry.*;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureSet;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.spell.ConjuringItem;

import javax.swing.*;
import java.util.Arrays;
import java.util.Optional;

public final class ModStructures {
    public static int RADIUS_BLOCKS = 1000;
    public static final Identifier GAZEBO_OF_SINS_ID = Identifier.of(Lulasmod.MOD_ID, "gazebo_of_sins");
    public static final RegistryKey<StructureSet> RING_SET_KEY = RegistryKey.of(RegistryKeys.STRUCTURE_SET, Identifier.of(Lulasmod.MOD_ID, "structure_ring"));
    private static final int count = (int) ModSpells.SpellTabItems.stream().filter(identifier -> !(Registries.ITEM.get(identifier) instanceof ConjuringItem)).count();
    private static final BlockPos[] GAZEBO_POS = new BlockPos[count];


    static {
        Arrays.setAll(GAZEBO_POS, value -> {
            double angle = (Math.PI * 2)  * ((double) value / count);
            return new BlockPos(
                (int) Math.round(Math.cos(angle) * RADIUS_BLOCKS),
                0,
                (int) Math.round(Math.sin(angle) * RADIUS_BLOCKS));
        });
    }

    public static void init(){
        ServerChunkEvents.CHUNK_LOAD.register((serverWorld, worldChunk) -> {
            for (BlockPos pos : GAZEBO_POS){
                if (worldChunk.getPos().equals(new ChunkPos(pos))){
                    if (serverWorld.getServer().isOnThread()) {
                        StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();
                        Optional<StructureTemplate> optional = Optional.empty();
                        try {
                            optional = structureTemplateManager.getTemplate(GAZEBO_OF_SINS_ID);
                        } catch (InvalidIdentifierException ignored) {
                        }

                        optional.ifPresent(structureTemplate -> structureTemplate.place(
                                serverWorld,
                                pos,
                                pos,
                                new StructurePlacementData(),
                                serverWorld.getRandom(),
                                Block.NOTIFY_LISTENERS | Block.FORCE_STATE_AND_SKIP_CALLBACKS_AND_DROPS
                        ));
                        break;
                    }
                }
            }
        });
    }
}