package net.spit365.lulasmod.state;

import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.spit365.lulasmod.util.MultiVec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LinkedLightningPersistentState extends PersistentState {
	public static final HashMap<Entity, MultiVec3d> lastLinks = new HashMap<>();
	private final Set<MultiVec3d> links = new HashSet<>();

	public static final PersistentState.Type<LinkedLightningPersistentState> TYPE =
		new PersistentState.Type<>(
			LinkedLightningPersistentState::new,
			LinkedLightningPersistentState::fromNbt,
			DataFixTypes.LEVEL
		);

	public LinkedLightningPersistentState() {}

	public static LinkedLightningPersistentState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		LinkedLightningPersistentState state = new LinkedLightningPersistentState();
		if (nbt.contains("links", NbtElement.LIST_TYPE)) {
			MultiVec3d.CODEC.listOf().parse(NbtOps.INSTANCE, nbt.getList("links", NbtElement.COMPOUND_TYPE))
				.resultOrPartial(System.err::println)
				.ifPresent(state.links::addAll);
		}
		return state;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		MultiVec3d.CODEC.listOf().encodeStart(NbtOps.INSTANCE, new ArrayList<>(this.links))
			.resultOrPartial(System.err::println)
			.ifPresent(listTag -> nbt.put("links", listTag));
		return nbt;
	}

	public static LinkedLightningPersistentState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(TYPE, "linked_lightning");
	}

	public Set<MultiVec3d> getLinks() {
		return this.links;
	}

	public void add(MultiVec3d ppw) {
		this.links.add(ppw);
		this.markDirty();
	}

	public void remove(MultiVec3d ppw) {
		this.links.remove(ppw);
		this.markDirty();
	}
}