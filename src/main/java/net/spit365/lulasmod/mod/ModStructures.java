package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.structure.Structure;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.spell.ConjuringItem;

import java.util.List;
import java.util.Optional;

public final class ModStructures {
    public static int RADIUS_BLOCKS = 1000;
    public static final Identifier GAZEBO_OF_SINS_ID = Identifier.of(Lulasmod.MOD_ID, "gazebo_of_sins");
    public static final RegistryKey<StructureSet> RING_SET_KEY = RegistryKey.of(RegistryKeys.STRUCTURE_SET, Identifier.of(Lulasmod.MOD_ID, "structure_ring"));

    private static int getCount() {
        return (int) ModSpells.SpellTabItems.stream().filter(identifier -> !(Registries.ITEM.get(identifier) instanceof ConjuringItem)).count();
    }

    public static void init(){
        DynamicRegistrySetupCallback.EVENT.register(view -> {
            Optional<Registry<Biome>> biomeRegistryOpt = view.getOptional(RegistryKeys.BIOME);
            Optional<Registry<Structure>> structureRegistryOpt = view.getOptional(RegistryKeys.STRUCTURE);
            Optional<Registry<StructureSet>> setRegistryOpt = view.getOptional(RegistryKeys.STRUCTURE_SET);

            if (biomeRegistryOpt.isEmpty() || structureRegistryOpt.isEmpty() || setRegistryOpt.isEmpty()) {
                Lulasmod.LOGGER.error("Missing one of the dynamic registries!");
                return;
            }

            RegistryEntryList.Named<Biome> preferredOverworld = biomeRegistryOpt.get().getOrThrow(BiomeTags.IS_OVERWORLD);
            RegistryEntry<Structure> myStructureEntry =
                    structureRegistryOpt.get().getEntry(GAZEBO_OF_SINS_ID)
                            .orElseThrow(() -> new IllegalStateException("Structure " + GAZEBO_OF_SINS_ID + " not found"));

            ConcentricRingsStructurePlacement placement = new ConcentricRingsStructurePlacement(
                    new Vec3i(0, 0, 0),
                    StructurePlacement.FrequencyReductionMethod.DEFAULT,
                    1.0f,
                    0,
                    Optional.empty(),
                    Math.round(RADIUS_BLOCKS / 16f),
                    1,
                    getCount(),
                    preferredOverworld
            );

            Registry.register(setRegistryOpt.get(), RING_SET_KEY.getValue(), new StructureSet(List.of(StructureSet.createEntry(myStructureEntry)), placement));
        });
    }
}