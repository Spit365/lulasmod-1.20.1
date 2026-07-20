package net.spit365.lulasmod.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.Presence;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModBlocks;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModItems;
import net.spit365.lulasmod.mod.ModSpells;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class RegisterHelper {
	public static <T> AttachmentType<T> attachmentType(String name, boolean deathPersistent) {
		AttachmentType<T> attachmentType = AttachmentRegistry.create(id(name));
		if (deathPersistent) ModData.deathPersistent.add((AttachmentType<Object>) attachmentType);
		return attachmentType;
	}

	public static <T extends Block> ModBlocks.BlockAndItem<T> block(String name, Function<BlockBehaviour.Properties, T> factory, BlockBehaviour.Properties settings) {
		ResourceLocation id = id(name);
		ResourceKey<Block> blockRegistryKey = ResourceKey.create(Registries.BLOCK, id);
		ResourceKey<Item> itemRegistryKey = ResourceKey.create(Registries.ITEM, id);
		T block = factory.apply(settings.setId(blockRegistryKey));
		BlockItem item = Registry.register(BuiltInRegistries.ITEM, itemRegistryKey, new BlockItem(block, new Item.Properties().setId(itemRegistryKey)));
		ModItems.MainTabItems.add(new ItemStack(item));
		return new ModBlocks.BlockAndItem<>(
			Registry.register(BuiltInRegistries.BLOCK, blockRegistryKey, block),
			item
		);
	}

	public static <T extends BlockEntity> BlockEntityType<T> blockEntity(String id, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(factory, blocks).build());
	}

	public static <T> DataComponentType<T> componentType(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id(name),
				builderOperator.apply(DataComponentType.builder()).build());
	}

	public static <T extends ConsumeEffect> ConsumeEffect.Type<T> consumeEffect(String name, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCode) {
		return Registry.register(BuiltInRegistries.CONSUME_EFFECT_TYPE, id(name), new ConsumeEffect.Type<>(codec, streamCode));
	}

	public static ResourceKey<DamageType> damageType(String name) {
		return ResourceKey.create(Registries.DAMAGE_TYPE, id(name));
	}

	public static ResourceKey<Level> dimension(String name) {
		return ResourceKey.create(Registries.DIMENSION, id(name));
	}

	public static <T extends Entity> EntityType<T> entity(String id, EntityType.EntityFactory<T> entityFactory, float width, float height, int maxTrackingRange, int trackingTickInterval) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(Registries.ENTITY_TYPE, id(id));
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, key,
			 EntityType.Builder.of(entityFactory, MobCategory.MISC)
				 .sized(width, height)
				 .clientTrackingRange(maxTrackingRange)
				 .updateInterval(trackingTickInterval)
				 .build(key));
	}

	public static GameRules.Key<GameRules.BooleanValue> gameRule(String name, GameRules.Category category, GameRules.Type<GameRules.BooleanValue> type) {
		return GameRuleRegistry.register(Lulasmod.MOD_ID + "." + name, category, type);
	}

	public static <T extends Item> T item(String name, Function<Item.Properties, T> factory, Item.Properties settings) {
		T item = itemInternal(name, factory, settings);
		ModItems.MainTabItems.add(new ItemStack(item));
		return item;
	}
	public static <T extends Item> T item(String name, Function<Item.Properties, T> factory, Item.Properties settings, BiConsumer<Item, List<ItemStack>> creativeInventoryStacks) {
		T item = itemInternal(name, factory, settings);
		List<ItemStack> list = new LinkedList<>();
		creativeInventoryStacks.accept(item, list);
		ModItems.MainTabItems.addAll(list);
		return item;
	}
	private static <T extends Item> T itemInternal(String name, Function<Item.Properties, T> factory, Item.Properties settings) {
		ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id(name));
		return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(settings.setId(key)));
	}

	public static CreativeModeTab itemGroup(String name, Item item, List<ItemStack> list) {
		return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id(name), FabricItemGroup.builder()
			.title(Component.translatable("item_group." + Lulasmod.MOD_ID + "." + name))
			.icon(() -> new ItemStack(item))
			.displayItems((displayContext, entries) -> entries.acceptAll(list.stream().peek(stack -> stack.setCount(1)).toList()))
			.build());
	}

	public static SimpleParticleType particle(String name, Boolean alwaysShow) {
		 SimpleParticleType particle = FabricParticleTypes.simple(alwaysShow);
		 Registry.register(BuiltInRegistries.PARTICLE_TYPE, id(name), particle);
		 return particle;
	}

	public static <T> AttachmentType<T> persistentAttachmentType(String name, Codec<T> codec, boolean deathPersistent) {
		AttachmentType<T> attachmentType = AttachmentRegistry.createPersistent(id(name), codec);
		if (deathPersistent) ModData.deathPersistent.add((AttachmentType<Object>) attachmentType);
		return attachmentType;
	}

	public static Presence presence(int levelRequirement) {
		return Presence.register(levelRequirement);
	}

	public static @NotNull <T extends AbstractContainerMenu, D> ExtendedScreenHandlerType<T, D> screenHandler(String name, ExtendedScreenHandlerType.ExtendedFactory<T, D> screenHandlerFactory, StreamCodec<ByteBuf, D> packetCodec) {
		return Registry.register(BuiltInRegistries.MENU, id(name), new ExtendedScreenHandlerType<>(screenHandlerFactory, packetCodec));
	}

	public static <T extends SpellItem> T spell(String name, Function<Item.Properties, T> factory) {
		T spell = itemInternal(name, factory, new Item.Properties().stacksTo(1));
		ModSpells.SpellTabItems.add(new ItemStack(spell));
		return spell;
	}

	public static Holder<MobEffect> statusEffect(String name, MobEffect effect) {
		Registry.register(BuiltInRegistries.MOB_EFFECT, id(name), effect);
		return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
	}

	private static @NotNull ResourceLocation id(String name) {
		return ResourceLocation.fromNamespaceAndPath(Lulasmod.MOD_ID, name);
	}
}
