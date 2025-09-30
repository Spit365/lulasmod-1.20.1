package net.spit365.lulasmod.manager;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModBlocks;
import net.spit365.lulasmod.mod.ModItems;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class RegisterHelper {
	public static <T> AttachmentType<T>	attachmentType(String name, Codec<T> codec){
		return AttachmentRegistry.createPersistent(Identifier.of(name, Lulasmod.MOD_ID), codec);
	}

	public static <T extends Block> ModBlocks.BlockAndItem<T> block(String name, Function<AbstractBlock.Settings, T> factory, AbstractBlock.Settings settings) {
		Identifier id = Identifier.of(Lulasmod.MOD_ID, name);
		ModItems.CreativeTabItems.add(id);
		RegistryKey<Block> blockRegistryKey = RegistryKey.of(RegistryKeys.BLOCK, id);
		RegistryKey<Item> itemRegistryKey = RegistryKey.of(RegistryKeys.ITEM, id);
		T block = factory.apply(settings.registryKey(blockRegistryKey));
		return new ModBlocks.BlockAndItem<>(
			Registry.register(Registries.BLOCK, blockRegistryKey, block),
			Registry.register(Registries.ITEM, itemRegistryKey, new BlockItem(block, new Item.Settings().registryKey(itemRegistryKey)))
		);
	}

	public static <T> ComponentType<T> componentType(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Lulasmod.MOD_ID, name),
				builderOperator.apply(ComponentType.builder()).build());
	}

	public static RegistryKey<DamageType> damageType(String name) {
		return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Lulasmod.MOD_ID, name));
	}

	public static RegistryKey<World> dimension(String name) {
		return RegistryKey.of(RegistryKeys.WORLD, Identifier.of(Lulasmod.MOD_ID, name));
	}

	public static <T extends Entity> EntityType<T> entity(String id, EntityType.EntityFactory<T> entityFactory, EntityRendererFactory<T> entityRendererFactory, float width, float height, int maxTrackingRange, int trackingTickInterval) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Lulasmod.MOD_ID, id));
		EntityType<T> entityType = Registry.register(Registries.ENTITY_TYPE, key,
				 EntityType.Builder.create(entityFactory, SpawnGroup.MISC)
						 .dimensions(width, height)
						 .maxTrackingRange(maxTrackingRange)
						 .trackingTickInterval(trackingTickInterval)
						 .build(key));
		 EntityRendererRegistry.register(entityType, entityRendererFactory);
		 return entityType;
	}

	public static GameRules.Key<GameRules.BooleanRule> gameRule(String name, GameRules.Category category, boolean defaultValue) {
		return GameRuleRegistry.register(name, category, GameRuleFactory.createBooleanRule(defaultValue));
	}

	public static <T extends Item> T item(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
		ModItems.CreativeTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
		return itemInternal(name, factory, settings);
	}
	private static <T extends Item> T itemInternal(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Lulasmod.MOD_ID, name));
		return Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
	}

	public static ItemGroup itemGroup(String name, Item item, List<Identifier> list) {
		return Registry.register(Registries.ITEM_GROUP, Identifier.of(Lulasmod.MOD_ID, name), FabricItemGroup.builder()
			.displayName(Text.translatable("item_group." + Lulasmod.MOD_ID + "." + name))
			.icon(() -> new ItemStack(item))
			.entries((displayContext, entries) -> {
				for (Identifier id : list) entries.add(Registries.ITEM.get(id));
			})
			.build());
	}

	public static KeyBinding keyBinding(String name, int key){
		return KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key." + Lulasmod.MOD_ID + "." + name,
			InputUtil.Type.KEYSYM,
			key,
			"key.categories." + Lulasmod.MOD_ID
		));
	}

	public static SimpleParticleType particle(String name, Boolean alwaysShow, ParticleFactoryRegistry.PendingParticleFactory<SimpleParticleType> render){
		 SimpleParticleType particle = FabricParticleTypes.simple(alwaysShow);
		 Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Lulasmod.MOD_ID, name), particle);
		 ParticleFactoryRegistry.getInstance().register(particle, render);
		 return particle;
	}

	public static <T extends SpellItem> T spell(String name, Function<Item.Settings, T> factory) {
		ModSpells.SpellTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
		return itemInternal(name, factory, new Item.Settings().maxCount(1));
	}

	public static RegistryEntry<StatusEffect> statusEffect(String id, StatusEffect effect) {
		Registry.register(Registries.STATUS_EFFECT, Identifier.of(Lulasmod.MOD_ID, id), effect);
		return Registries.STATUS_EFFECT.getEntry(effect);
	}
}
