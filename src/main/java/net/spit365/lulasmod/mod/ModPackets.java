package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModPackets {
     public record CYCLE_PLAYER_SPELL_C2S() implements CustomPayload {
          private CYCLE_PLAYER_SPELL_C2S(PacketByteBuf buf) {this();}
          public static final Id<CYCLE_PLAYER_SPELL_C2S> ID = new Id<>(Identifier.of(Server.MOD_ID, "cycle_player_spell"));
          public static final PacketCodec<PacketByteBuf, CYCLE_PLAYER_SPELL_C2S> CODEC = CustomPayload.codecOf(CYCLE_PLAYER_SPELL_C2S::write, CYCLE_PLAYER_SPELL_C2S::new);

          private void write(PacketByteBuf buf) {}

          @Override
          public CustomPayload.Id<CYCLE_PLAYER_SPELL_C2S> getId() {return ID;}
     }

     public record SPELL_HOTBAR_LIST_S2C(List<Identifier> list) implements CustomPayload{
          public static final Id<SPELL_HOTBAR_LIST_S2C> ID = new Id<>(Identifier.of(Server.MOD_ID, "spell_hotbar_list"));
          private static final PacketCodec<PacketByteBuf, SPELL_HOTBAR_LIST_S2C> CODEC = CustomPayload.codecOf(SPELL_HOTBAR_LIST_S2C::write, SPELL_HOTBAR_LIST_S2C::read);
          private void write(PacketByteBuf buf) {
               Map<Integer, Identifier> map = new HashMap<>();
               for (int i = 0; i < list.size(); i++)
                    map.put(i, list.get(i));
               buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeIdentifier);
          }
          private static SPELL_HOTBAR_LIST_S2C read(PacketByteBuf buf) {
               List<Identifier> list = new LinkedList<>();
               Map<Integer, Identifier> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readIdentifier);
               for (int i = 0; i < map.size(); i++) list.add(map.get(i));
               return new SPELL_HOTBAR_LIST_S2C(list);
          }

          @Override
          public Id<? extends CustomPayload> getId() {return ID;}
     }     
     public record TAILED_PLAYER_LIST_S2C(List<String> list) implements CustomPayload {
          public static final Id<TAILED_PLAYER_LIST_S2C> ID = new Id<>(Identifier.of(Server.MOD_ID, "tailed_player_list"));
          private static final PacketCodec<PacketByteBuf, TAILED_PLAYER_LIST_S2C> CODEC = CustomPayload.codecOf(TAILED_PLAYER_LIST_S2C::write, TAILED_PLAYER_LIST_S2C::read);

          private void write(PacketByteBuf buf) {
               Map<Integer, String> map = new HashMap<>();
               for (int i = 0; i < list.size(); i++)
                    map.put(i, list.get(i));
               buf.writeMap(map, PacketByteBuf::writeInt, PacketByteBuf::writeString);
          }
          private static TAILED_PLAYER_LIST_S2C read(PacketByteBuf buf) {
               List<String> list = new LinkedList<>();
               Map<Integer, String> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readString);
               for (int i = 0; i < map.size(); i++) list.add(map.get(i));
               return new TAILED_PLAYER_LIST_S2C(list);
          }

          @Override
          public Id<? extends CustomPayload> getId() {return ID;}
     }
     public record TIME_FORWARD_ANIMATION_S2C() implements CustomPayload {
          private TIME_FORWARD_ANIMATION_S2C(PacketByteBuf buf) {this();}
          public static final Id<TIME_FORWARD_ANIMATION_S2C> ID = new Id<>(Identifier.of(Server.MOD_ID, "time_forward_animation"));
          public static final PacketCodec<PacketByteBuf, TIME_FORWARD_ANIMATION_S2C> CODEC = CustomPayload.codecOf(TIME_FORWARD_ANIMATION_S2C::write, TIME_FORWARD_ANIMATION_S2C::new);

          private void write(PacketByteBuf buf) {}

          @Override
          public CustomPayload.Id<TIME_FORWARD_ANIMATION_S2C> getId() {return ID;}
     }

     public static void init(){
          PayloadTypeRegistry.playC2S().register(CYCLE_PLAYER_SPELL_C2S.ID, CYCLE_PLAYER_SPELL_C2S.CODEC);
          PayloadTypeRegistry.playS2C().register(SPELL_HOTBAR_LIST_S2C.ID, SPELL_HOTBAR_LIST_S2C.CODEC);
          PayloadTypeRegistry.playS2C().register(TAILED_PLAYER_LIST_S2C.ID, TAILED_PLAYER_LIST_S2C.CODEC);
          PayloadTypeRegistry.playS2C().register(TIME_FORWARD_ANIMATION_S2C.ID, TIME_FORWARD_ANIMATION_S2C.CODEC);
     }
}
