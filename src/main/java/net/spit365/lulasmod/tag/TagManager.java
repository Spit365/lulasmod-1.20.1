package net.spit365.lulasmod.tag;

import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import java.util.LinkedList;

public class TagManager {

    private static String c(TagCategory category){return category.identifier() + ": ";}

    public static void put(Entity entity, TagCategory category, Identifier value){
        remove(entity, category);
        entity.addCommandTag(c(category) + value.getNamespace() + ":" + value.getPath());
    }
    public static void put(Entity entity, TagCategory category, LinkedList<Identifier> list){
        remove(entity, category);
        StringBuilder stringBuilder = new StringBuilder(c(category));
        for (Identifier id : list){
            stringBuilder.append(";").append(id);
        }
        entity.addCommandTag(stringBuilder.toString());
    }
    public static Identifier read(Entity entity, TagCategory category){
        for (String tag : entity.getCommandTags()){
            if (tag.contains(c(category))){
                String[] s = tag.replace(c(category), "").split(":");
                return new Identifier(s[0], s[1]);
            }
        }
        return null;
    }
    public static LinkedList<Identifier> readList(Entity entity, TagCategory category){
        for (String tag : entity.getCommandTags()){
            if (tag.contains(c(category))) {
                LinkedList<Identifier> list = new LinkedList<>();
                for (String s1 : tag.replace(c(category), "").split(";")){
                    String[] s2 = s1.split(":");
                    if (s2.length == 2) list.add(new Identifier(s2[0], s2[1]));
                }
                return list;
            }
        }
        return new LinkedList<>();
    }
    public static Boolean check(Entity entity, TagCategory category, Identifier value){
        Identifier id = read(entity, category);
        if (id == null) return false; else return id.equals(value);
    }
    public static void remove(Entity entity, TagCategory category){
        entity.getCommandTags().removeIf(tag -> tag.contains(c(category)));
    }
    public static void cycle(Entity entity, TagCategory category) {
        LinkedList<Identifier> list = readList(entity, category);
        if (!list.isEmpty()) {
            Identifier first = list.pollFirst();
            list.addLast(first);
            put(entity, category, list);
        }
    }
}
