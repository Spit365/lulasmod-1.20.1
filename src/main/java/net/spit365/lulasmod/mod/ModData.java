package net.spit365.lulasmod.mod;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.spit365.lulasmod.block.SpellPedestalBlock;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.custom.TimeForward;
import net.spit365.lulasmod.util.RegisterHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ModData {
	public static final Codec<Shimmer.Variant> SHIMMER_VARIANT_CODEC = Codecs.rangedInt(0, Shimmer.Variant.values().length - 1).xmap(Shimmer.Variant::byIndex, Shimmer.Variant::getIndex);

	public static final Set<AttachmentType<Object>> deathPersistent = new HashSet<>();

    public static final ComponentType<List<Identifier>> SPELL_BOOK_SPELLS = RegisterHelper.componentType("spell_book_spells", builder -> builder.codec(Identifier.CODEC.listOf()));
	public static final ComponentType<Shimmer.Variant> SHIMMER_VARIANT = RegisterHelper.componentType("shimmer_variant", builder -> builder.codec(SHIMMER_VARIANT_CODEC));

	public static final AttachmentType<Integer> DAMAGE_DELAY = RegisterHelper.persistentAttachmentType("damage_delay", Codec.INT, false);
	public static final AttachmentType<List<Identifier>> EQUIPPED_SPELLS = RegisterHelper.persistentAttachmentType("equipped_spells", Identifier.CODEC.listOf(), true);
	public static final AttachmentType<Integer> DASH_SPELL = RegisterHelper.persistentAttachmentType("purloining_spell", Codec.INT, false);
	public static final AttachmentType<Integer> DASH_COOLDOWN = RegisterHelper.attachmentType("dash_cooldown", false);
	public static final AttachmentType<List<SpellPedestalBlock.WorldBlockPos>> ABSORBED_PEDESTALS = RegisterHelper.persistentAttachmentType("absorbed_pedestals", SpellPedestalBlock.WorldBlockPos.CODEC.listOf(), false);
	public static final AttachmentType<TimeForward.VisualContext> TIME_FORWARD_ANIMATION_FRAMES = RegisterHelper.attachmentType("time_forward_animation_frames", false);
	public static final AttachmentType<Integer> BLEED_VALUE = RegisterHelper.persistentAttachmentType("bleed_value", Codec.INT, false);
    public static final AttachmentType<Integer> SMOKE_SPELL_COOLDOWN = RegisterHelper.attachmentType("smoke_spell_cooldown", false);
    public static final AttachmentType<Boolean> DEMON = RegisterHelper.persistentAttachmentType("demon", Codec.BOOL, true);
	public static final AttachmentType<Shimmer.Variant> APPLIED_SHIMMER_VARIANT = RegisterHelper.persistentAttachmentType("applied_shimmer_variant", SHIMMER_VARIANT_CODEC, false);

	public static void init() {
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> deathPersistent.forEach(attachmentType -> newPlayer.setAttached(attachmentType, oldPlayer.getAttached(attachmentType))));
	}
}