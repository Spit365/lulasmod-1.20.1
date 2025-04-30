package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.tag.TagManager;

import java.util.HashMap;

public class ModMethods {
    private record ImpaledContext(PlayerEntity player, LivingEntity livingEntity, ParticleEffect particle, Integer bleedBuildup) {}
    private static final HashMap<ImpaledContext, Integer> impaled = new HashMap<>();
    private static int impaledCounter = 0;
    
    public static Boolean impale(PlayerEntity player, Item item, Integer baseCooldown, Integer maxCooldown, Integer iterations, ParticleEffect particle, Integer bleedBuildup){
        double radius = 5;
        player.getItemCooldownManager().set(item, 2);
        Vec3d selectionCenter = player.getRotationVec(1).normalize().multiply(radius).add(player.getPos());
        LivingEntity selectedEntity = null;
        for (Entity entity : player.getWorld().getOtherEntities(player, new Box(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))){
            if ((selectedEntity == null || selectedEntity.getPos().squaredDistanceTo(player.getPos()) > entity.getPos().squaredDistanceTo(player.getPos())) && entity instanceof LivingEntity){
                selectedEntity = (LivingEntity) entity;
            }
        }
        if (selectedEntity != null){
            selectedEntity.requestTeleport(selectedEntity.getX(), selectedEntity.getY() + 5, selectedEntity.getZ());
            player.getItemCooldownManager().set(item, maxCooldown);
            impaled.put(new ImpaledContext(player, selectedEntity, particle, bleedBuildup), iterations);
        }else player.getItemCooldownManager().set(item, baseCooldown -2);
        return selectedEntity != null;
    }

    public static void updateImpaled(){
        for (ImpaledContext context : impaled.keySet()) {
            LivingEntity victim = context.livingEntity();
            if (impaled.get(context) > 0 && victim.isAlive()) {
                if (!(victim instanceof EndermanEntity)) {
                    victim.setVelocity(0, 0, 0);
                    impaledCounter++;
                    if (impaledCounter >= 5) {
                        Vec3d pos = new Vec3d(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1).normalize().multiply(5).add(victim.getPos());
                        ParticleProjectileEntity projectile = (context.bleedBuildup > 0?
                                new ParticleProjectileEntity(victim.getWorld(), context.player(),  context.bleedBuildup) :
                                new ParticleProjectileEntity(victim.getWorld(), context.player(),  context.particle));
                        projectile.setNoGravity(true);
                        projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        TagManager.put(projectile, ModTagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                        projectile.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                        projectile.addVelocity(pos.subtract(victim.getPos()).multiply(-0.5));
                        victim.getWorld().spawnEntity(projectile);
                        impaled.put(context, impaled.get(context) -1);
                        impaledCounter = 0;
                    }
                } else victim.kill();
            } else impaled.remove(context);
        }
    }
    
    public static void applyBleed(LivingEntity entity, Integer duration){
        StatusEffectInstance effectInstance = entity.getStatusEffect(ModStatusEffects.BLEEDING);
        if (effectInstance != null){
            entity.setStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, effectInstance.getDuration() + duration), entity);
        } else entity.addStatusEffect(new StatusEffectInstance(ModStatusEffects.BLEEDING, duration));
    }
}
