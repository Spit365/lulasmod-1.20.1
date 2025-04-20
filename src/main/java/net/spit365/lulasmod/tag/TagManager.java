package net.spit365.lulasmod.tag;

import net.minecraft.entity.Entity;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TagManager {

    private static String category(String namespace, TagCategory category){return namespace + category.identifier() + ": ";}

    public static void put(String namespace, Entity entity, TagCategory category, String value){
        remove(namespace, entity, category);
        entity.addCommandTag(category(namespace, category) + value);
    }
    public static void put(String namespace, Entity entity, TagCategory category, List<String> list){
        remove(namespace, entity, category);
        StringBuilder stringBuilder = new StringBuilder(category(namespace, category));
        for (String string : list){
            stringBuilder.append("::").append(string);
        }
        entity.addCommandTag(stringBuilder.toString());
    }
    public static String read(String namespace, Entity entity, TagCategory category){
        for (String tag : entity.getCommandTags()){
            if (tag.contains(category(namespace, category))){
                return tag.replace(category(namespace, category), "");
            }
        }
        return null;
    }
    public static LinkedList<String> readList(String namespace, Entity entity, TagCategory category){
        if (read(namespace, entity, category) != null) {
            LinkedList<String> list = new LinkedList<>(Arrays.asList(read(namespace, entity, category).split("::")));
            list.remove(0);
            return list;
        }else return new LinkedList<>();
    }
    public static Boolean check(String namespace, Entity entity, TagCategory category, String value){
        String string = read(namespace, entity, category);
        if (string == null) return false; else return string.equals(value);
    }
    public static void remove(String namespace, Entity entity, TagCategory category){
        entity.getCommandTags().removeIf(tag -> tag.contains(category(namespace, category)));
    }
    public static void cycle(String namespace, Entity entity, TagCategory category) {
        LinkedList<String> list = new LinkedList<>(readList(namespace, entity, category));
        if (!list.isEmpty()) {
            String first = list.pollFirst();
            list.addLast(first);
            put(namespace, entity, category, list);
        }
    }
}
