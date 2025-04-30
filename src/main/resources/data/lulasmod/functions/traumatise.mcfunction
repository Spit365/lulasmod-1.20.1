# Select entities within 15 blocks, excluding the player
execute at @s as @e[distance=..15,tag=!tailed,type=!item] run tag @s add traumatised

# Play sound effect at the player's position
playsound minecraft:entity.ghast.hurt master @s ~ ~ ~

# Spawn particles around the affected area
execute at @s run particle minecraft:snowflake ~ ~ ~ 1 1 1 0.1 100
execute at @e[tag=traumatised] run particle minecraft:splash ~ ~ ~ 1 1 1 0.1 100
