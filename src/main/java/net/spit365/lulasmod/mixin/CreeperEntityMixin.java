package net.spit365.lulasmod.mixin;

import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.ModEntities;
import net.spit365.lulasmod.custom.ModImportant;
import net.spit365.lulasmod.custom.ModEntities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

import static net.spit365.lulasmod.Lulasmod.*;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SkinOverlayOwner {

	@Shadow
	private int lastFuseTime;

	@Shadow
	private int fuseTime;

	@Shadow
	private int explosionRadius;

	@Unique
	private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
	@Unique
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	@Unique
	private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	@Unique
	private int headsDropped;

	protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "initDataTracker", at = @At("RETURN"))
	private void addCustomTrackedData(CallbackInfo ci) {
		this.dataTracker.startTracking(FUSE_SPEED, -1);
		this.dataTracker.startTracking(CHARGED, false);
		this.dataTracker.startTracking(IGNITED, false);
	}

	@Inject(method = "initGoals", at = @At("HEAD"))
	private void addCustomGoals(CallbackInfo ci) {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new CreeperIgniteGoal((CreeperEntity) (Object) this));
		this.goalSelector.add(3, new FleeEntityGoal<>(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(3, new FleeEntityGoal<>(this, CatEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
		this.targetSelector.add(2, new RevengeGoal(this));
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	private void readCustomData(NbtCompound nbt, CallbackInfo ci) {
		this.dataTracker.set(CHARGED, nbt.getBoolean("powered"));
		if (nbt.contains("Fuse", NbtElement.NUMBER_TYPE)) {
			this.fuseTime = nbt.getShort("Fuse");
		}
		if (nbt.contains("ExplosionRadius", NbtElement.NUMBER_TYPE)) {
			this.explosionRadius = nbt.getByte("ExplosionRadius");
		}
		if (nbt.getBoolean("ignited")) {
			this.ignite();
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	private void writeCustomData(NbtCompound nbt, CallbackInfo ci) {
		if (this.dataTracker.get(CHARGED)) {
			nbt.putBoolean("powered", true);
		}
		nbt.putShort("Fuse", (short) this.fuseTime);
		nbt.putByte("ExplosionRadius", (byte) this.explosionRadius);
		nbt.putBoolean("ignited", this.isIgnited());
	}

	@Inject(method = "explode", at = @At("HEAD"), cancellable = true)
	private void onExplode(CallbackInfo ci) {
		if (!this.getWorld().isClient) {
			if (this.getType() != ModEntities.SMOKE_CREEPER) {
				ServerWorld serverWorld = (ServerWorld) this.getWorld();
				serverWorld.spawnParticles(ParticleTypes.HEART, this.getX(), this.getY(), this.getZ(), 2, 1, 1, 1, 0);
				List<? extends PlayerEntity> players = this.getWorld().getPlayers();
				for (int i = 0; i <= players.size() - 1; i++) {
					if (
							Objects.equals(players.get(i).getName().getString(), whoExplode)
							&&
							this.getPos().squaredDistanceTo(players.get(i).getPos()) <= 25
							&&
							ModImportant.creeperExplode
					) {
						players.get(i).kill();
						this.dead = true;
						this.discard();
					}
				}
			}else{
				ModImportant.summonSmoke(this.getPos(), this.getWorld());
                this.dead = true;
                this.discard();
			}
			ci.cancel();
		}
	}

	@Unique
	public boolean isIgnited() {
		return this.dataTracker.get(IGNITED);
	}

	@Unique
	public void ignite() {
		this.dataTracker.set(IGNITED, true);
	}
}
