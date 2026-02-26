package net.spit365.lulasmod.consumeeffect;

import com.mojang.serialization.*;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.RegisterHelper;

import java.util.List;
import java.util.stream.Stream;

public class ShimmerSyringeEffect implements ConsumeEffect {

    @Override
    public Type<? extends ConsumeEffect> getType() {
        return TYPE;
    }

    @Override
    public boolean onConsume(World world, ItemStack stack, LivingEntity user) {
        return false;
    }

    private static final MapCodec<ShimmerSyringeEffect> MAP_CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<ShimmerSyringeEffect> decode(DynamicOps<T> ops, MapLike<T> input) {
            return null;
        }

        @Override
        public <T> RecordBuilder<T> encode(ShimmerSyringeEffect input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return null;
        }
    };
    public static final ConsumableComponent SHIMMER_SYRINGE_CONSUMABLE = new ConsumableComponent(ConsumableComponent.DEFAULT_CONSUME_SECONDS, UseAction.DRINK, RegistryEntry.of(SoundEvents.BLOCK_GLASS_BREAK), false, List.of());
    public static final ConsumeEffect.Type<ShimmerSyringeEffect> TYPE = RegisterHelper.consumeEffect("shimmer_syringe_effect", MAP_CODEC, PacketCodec.unit(null));
}
