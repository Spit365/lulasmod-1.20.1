package net.spit365.lulasmod.custom.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;

import java.util.Objects;

public class LasciviousnessItem extends Item {

    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public LasciviousnessItem(Settings settings) {
        super(settings);
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Tool modifier", 5.0d, EntityAttributeModifier.Operation.ADDITION)
        );
        this.attributeModifiers = builder.build();
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()) {
            player.getItemCooldownManager().set(this, 2);
            double radius = 5d;
            Vec3d selectionCenter = player.getRotationVec(1).normalize().multiply(radius).add(player.getPos());
            LivingEntity selectedEntity = null;
            for (Entity entity : world.getOtherEntities(player, new Box(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))) {
                if ((selectedEntity == null || selectedEntity.getPos().squaredDistanceTo(player.getPos()) > entity.getPos().squaredDistanceTo(player.getPos())) && entity instanceof LivingEntity) {
                    selectedEntity = (LivingEntity) entity;
                }
            }
            if (selectedEntity == null) selectedEntity = player; else player.getItemCooldownManager().set(this, 50);
            TagManager.put(player, ModTagCategories.LASCIVIOUSNESS_TARGET, new Identifier(Lulasmod.MOD_ID, String.valueOf(selectedEntity.getId())));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker){
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        LivingEntity symbiotic = attacker;
        Entity temp = attacker.getWorld().getEntityById(Integer.parseInt(Objects.requireNonNull(TagManager.read(attacker, ModTagCategories.LASCIVIOUSNESS_TARGET)).getPath()));
        if (temp != null) symbiotic = (LivingEntity) temp;
        if (symbiotic.getHealth() < symbiotic.getMaxHealth()) symbiotic.heal(2f);
        Vec3d pos = attacker.getRotationVec(1).normalize().add(attacker.getPos());
        if (attacker.getWorld() instanceof ServerWorld serverWorld) serverWorld.spawnParticles(ParticleTypes.SONIC_BOOM, pos.getX(), pos.getY() + attacker.getEyeHeight(attacker.getPose()), pos.getZ(), 1, 0, 0, 0, 0);
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }
}
