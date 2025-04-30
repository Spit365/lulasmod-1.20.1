package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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

import java.util.LinkedList;

public class ModMethods {
    public static Boolean a(PlayerEntity player, Item item, Integer baseCooldown, Integer maxCooldown, Double radius, Integer iterations, Double distance, Double velocityMultiplier, ParticleEffect particle){
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
            LinkedList<ParticleProjectileEntity> list = new LinkedList<>();
            if (iterations <= 0) iterations = (int) selectedEntity.getHealth();
            for (int i = 0; i < iterations ; i++) {
                if (selectedEntity.isAlive()) {
                    if (!(selectedEntity instanceof EndermanEntity)) {
                        selectedEntity.setVelocity(0, 0, 0);
                        Vec3d pos = new Vec3d(Math.random() * 2 - 1, Math.random() * 2 - 1, Math.random() * 2 - 1).normalize().multiply(distance).add(selectedEntity.getPos());
                        ParticleProjectileEntity projectile = new ParticleProjectileEntity(selectedEntity.getWorld(), player, particle);
                        list.add(projectile);
                        projectile.setNoGravity(true);
                        projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        TagManager.put(projectile, ModTagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                        projectile.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                        projectile.addVelocity(pos.subtract(selectedEntity.getPos()).multiply(-velocityMultiplier));
                        selectedEntity.getWorld().spawnEntity(projectile);
                    }else {
                        selectedEntity.kill();
                        break;
                    }
                } else break;
            }
            for (ParticleProjectileEntity p : list) p.kill();
        }else player.getItemCooldownManager().set(item, baseCooldown -2);
        return selectedEntity != null;
    }
}
