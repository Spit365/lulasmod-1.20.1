package net.spit365.lulasmod.state;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.spit365.lulasmod.util.MultiVec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LinkedLightningPersistentState extends SavedData {
	public static final HashMap<Entity, MultiVec3d> lastLinks = new HashMap<>();
	private final Set<MultiVec3d> links = new HashSet<>();

	public static final Codec<LinkedLightningPersistentState> CODEC = MultiVec3d.CODEC.listOf().xmap(list -> {
		LinkedLightningPersistentState storage = new LinkedLightningPersistentState();
		storage.links.addAll(list);
		return storage;
	}, storage -> new ArrayList<>(storage.links));

	public static final SavedDataType<LinkedLightningPersistentState> TYPE =
		new SavedDataType<>(
			"linked_lightning",
			ctx -> new LinkedLightningPersistentState(),
			ctx -> CODEC,
			DataFixTypes.LEVEL
		);

	public static LinkedLightningPersistentState get(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(TYPE);
	}

	public Set<MultiVec3d> getLinks() {
		return this.links;
	}

	public void add(MultiVec3d ppw) {
		this.links.add(ppw);
		this.setDirty();
	}
	public void remove(MultiVec3d ppw) {
		this.links.remove(ppw);
		this.setDirty();
	}
}
