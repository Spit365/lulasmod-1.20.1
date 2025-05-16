package net.spit365.lulasmod.mixin;

import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.Mod;
import net.spit365.lulasmod.tag.TagManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {

    public LivingEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getAttacker() != null) {
            Identifier read = TagManager.read(source.getAttacker(), Mod.TagCategories.DAMAGE_DELAY);
            if (read != null) timeUntilRegen = Integer.parseInt(read.getPath());
        }
        if (source.getTypeRegistryEntry().matchesKey(Mod.DamageSources.BLOODSUCKING)) timeUntilRegen = 0;
    }
}