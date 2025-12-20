package net.spit365.lulasmod.mod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.RegisterHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModData {
	public static final Set<AttachmentType<Object>> deathPersistent = new HashSet<>();

    public static final ComponentType<List<Identifier>> SPELL_BOOK_SPELLS = RegisterHelper.componentType("spell_book_spells", builder -> builder.codec(Identifier.CODEC.listOf()));

	public static final AttachmentType<Integer> DAMAGE_DELAY = RegisterHelper.attachmentType("damage_delay", Codec.INT, false);
	public static final AttachmentType<List<Identifier>> EQUIPPED_SPELLS = RegisterHelper.attachmentType("equipped_spells", Identifier.CODEC.listOf(), true);
	public static final AttachmentType<Integer> DASH_SPELL = RegisterHelper.attachmentType("purloining_spell", Codec.INT, false);
	public static final AttachmentType<List<WorldBlockPos>> ABSORBED_PEDESTALS = RegisterHelper.attachmentType("absorbed_pedestals", WorldBlockPos.CODEC.listOf(), false);
	public static final AttachmentType<Integer> TIME_FORWARD_ANIMATION_FRAMES = RegisterHelper.attachmentType("absorbed_pedestals", Codec.INT, false);
	public static final AttachmentType<Integer> BLEED_VALUE = RegisterHelper.attachmentType("bleed_value", Codec.INT, false);
    public static final AttachmentType<Integer> SMOKE_SPELL_COOLDOWN = RegisterHelper.attachmentType("smoke_spell_cooldown", Codec.INT, false);
    public static final AttachmentType<Boolean> DEMON = RegisterHelper.attachmentType("demon", Codec.BOOL, true);

	public record WorldBlockPos(RegistryKey<World> world, BlockPos blockPos){
		public static final Codec<WorldBlockPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("world").xmap(id -> RegistryKey.of(RegistryKeys.WORLD, id), RegistryKey::getValue)
				.forGetter(WorldBlockPos::world),
			BlockPos.CODEC.fieldOf("blockPos")
				.forGetter(WorldBlockPos::blockPos)
		).apply(instance, WorldBlockPos::new));
	}

	public static void init() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> deathPersistent.forEach(attachmentType -> newPlayer.setAttached(attachmentType, oldPlayer.getAttached(attachmentType))));
	}
}