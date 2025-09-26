package net.spit365.lulasmod.mod;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Server;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {
    public static final ComponentType<List<Identifier>> SPELL_BOOK_SPELLS =
            register("spells", builder -> builder.codec(Identifier.CODEC.listOf()));


    private static <T>ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Server.MOD_ID, name),
                builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {}
}