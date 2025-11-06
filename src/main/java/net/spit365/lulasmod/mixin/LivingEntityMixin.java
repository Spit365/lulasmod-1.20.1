package net.spit365.lulasmod.mixin;

import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> entityType, World world) {super(entityType, world);}

    @Shadow public abstract ItemStack getMainHandStack();
    @Shadow public abstract boolean isUsingItem();

    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		Entity attacker = source.getAttacker();
        if (source.getTypeRegistryEntry().matchesKey(ModDamageSources.BLOODSUCKING)) timeUntilRegen = 0;
		if (attacker != null) {
            Integer i = attacker.getAttached(ModData.DAMAGE_DELAY);
            if (i != null) timeUntilRegen = i;
        }
    }
    @ModifyVariable(method = "travelMidAir", at = @At("STORE"), ordinal = 0)
    private double travelMidAir(double d){
        LivingEntityMixin entity = this;
        return  (
            entity.getMainHandStack().getItem() instanceof BowItem &&
            entity.isUsingItem() &&
            !entity.isOnGround() &&
            entity.getVelocity().y <= 0.0
        ) ? -0.07 : d;
    }
}