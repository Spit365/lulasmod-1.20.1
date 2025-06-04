package net.spit365.lulasmod.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TickerManager<T>{
     private static final Set<Ticker<?>> tickers = new HashSet<>();

     public static class Ticker<I>{
          public Consumer<I> output;
          private final Class<I> input;

          private Ticker(Class<I> input, Consumer<I> output) {
               this.output = output;
               this.input = input;
               tickers.add(this);
          }

          public void tick(Object input) {
               if (this.input.isInstance(input)) output.accept(this.input.cast(input));
               else output.accept(null);
          }
     }
     public static void tickAll(Object input){
          tickers.forEach(ticker -> ticker.tick(input));
     }

     public static <I> Ticker<I> createTicker(Class<I> type, Consumer<I> tick){
          return new Ticker<>(type, tick);
     }
}
