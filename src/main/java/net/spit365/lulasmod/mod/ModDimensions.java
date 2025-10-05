package net.spit365.lulasmod.mod;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.manager.RegisterHelper;

public class ModDimensions {
	public static final RegistryKey<World> POCKET_DIMENSION = RegistryKey.of(RegistryKeys.WORLD, Identifier.of(Lulasmod.MOD_ID, "pocket_dimension"));
}
