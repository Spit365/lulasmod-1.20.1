package net.spit365.lulasmod.custom.manager;

import net.minecraft.entity.Entity;

public class TagManager {
    public static void put(Entity entity, String category, String value){
        entity.addCommandTag(category +  ": " + value);
    }
    public static String read(Entity entity, String category){
        String value = null;
        for (String tag : entity.getCommandTags()){
            if (tag.contains(category + ": ")){
                value = tag.replace(category + ": ", "");
            }
        }
        return value;
    }
    public static void remove(Entity entity, String category){
        entity.getCommandTags().removeIf(tag -> tag.contains(category + ": "));
    }
    public static Boolean check(Entity entity, String category, String value){
        String string = read(entity, category);
        if (string == null) return false; else return string.equals(value);
    }
}
