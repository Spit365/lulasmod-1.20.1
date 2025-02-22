# Select entities within 15 blocks, excluding the player
execute as @e[distance=..15,tag=!tailed,type=!item] at @s run tag @s add immobilized

# Play sound effect at the player's position
execute at @e[tag=immobilized] run playsound minecraft:entity.evoker.prepare_attack master @s ~ ~ ~

# Spawn particles around the affected area
execute at @s run particle minecraft:enchant ~ ~ ~ 1 1 1 0.1 100
