package net.spit365.lulasmod.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TickerManager<T>{
     private static Set<Ticker<?>> tickers = new HashSet<>();

     public static class Ticker<I>{
          public Consumer<I> tick;
          private final Class<I> type;

          public Ticker(Class<I> type, Consumer<I> tick) {
               this.tick = tick;
               this.type = type;
               tickers.add(this);
          }

          public void tick(Object input) {
               if (type.isInstance(input)) tick.accept(type.cast(input));
               else tick.accept(null);
          }
     }
     public static void tickAll(Object input){
          tickers.forEach(ticker -> ticker.tick(input));
     }

     public static <I> Ticker<I> createTicker(Class<I> type, Consumer<I> tick){
          return new Ticker<>(type, tick);
     }
}
