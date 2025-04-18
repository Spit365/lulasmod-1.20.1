package net.spit365.lulasmod.tag;

import net.minecraft.entity.Entity;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TagManager {
    private static final String divider = ": ";
    public static void put(Entity entity, TagCategory category, String value){
        remove(entity, category);
        entity.addCommandTag(category.identifier() + divider + value);
    }
    public static void put(Entity entity, TagCategory category, List<String> list){
        remove(entity, category);
        StringBuilder stringBuilder = new StringBuilder(category.identifier() + divider);
        for (String string : list){
            stringBuilder.append("::").append(string);
        }
        entity.addCommandTag(stringBuilder.toString());
    }
    public static String read(Entity entity, TagCategory category){
        for (String tag : entity.getCommandTags()){
            if (tag.contains(category.identifier() + divider)){
                return tag.replace(category.identifier() + divider, "");
            }
        }
        return null;
    }
    public static LinkedList<String> readList(Entity entity, TagCategory category){
        if (read(entity, category) != null) {
            LinkedList<String> list = new LinkedList<>(Arrays.asList(read(entity, category).split("::")));
            list.remove(0);
            return list;
        }else return new LinkedList<>();
    }
    public static Boolean check(Entity entity, TagCategory category, String value){
        String string = read(entity, category);
        if (string == null) return false; else return string.equals(value);
    }
    public static Boolean check(Entity entity, TagCategory category, List<String> list){
        return list.equals(readList(entity, category));
    }
    public static Boolean checkList(Entity entity, TagCategory category, String value){
        return readList(entity, category).contains(value);
    }
    public static void remove(Entity entity, TagCategory category){
        entity.getCommandTags().removeIf(tag -> tag.contains(category.identifier() + divider));
    }
    public static void cycle(Entity entity, TagCategory category) {
        LinkedList<String> list = new LinkedList<>(readList(entity, category));
        if (!list.isEmpty()) {
            String first = list.pollFirst();
            list.addLast(first);
        }
        put(entity, category, list);
    }
}
