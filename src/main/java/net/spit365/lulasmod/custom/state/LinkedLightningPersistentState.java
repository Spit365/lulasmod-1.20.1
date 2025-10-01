package net.spit365.lulasmod.custom.state;

import com.mojang.serialization.Codec;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.spit365.lulasmod.manager.MultiVec3d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LinkedLightningPersistentState extends PersistentState {
	private final Set<MultiVec3d> links = new HashSet<>();

	public static final Codec<LinkedLightningPersistentState> CODEC = MultiVec3d.CODEC.listOf().xmap(list -> {
		LinkedLightningPersistentState storage = new LinkedLightningPersistentState();
		storage.links.addAll(list);
		return storage;
	}, storage -> new ArrayList<>(storage.links));

	public static final PersistentStateType<LinkedLightningPersistentState> TYPE =
		new PersistentStateType<>(
			"linked_lightning",
			ctx -> new LinkedLightningPersistentState(),
			ctx -> CODEC,
			DataFixTypes.LEVEL
		);

	public static LinkedLightningPersistentState get(ServerWorld world) {
		return world.getPersistentStateManager().getOrCreate(TYPE);
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
