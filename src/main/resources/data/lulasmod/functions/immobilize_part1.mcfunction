# Select entities within 15 blocks, excluding the player
execute as @e[distance=..50,tag=!tailed,type=!item] at @s run tag @s add immobilized

# Play sound effect at the player's position
execute as @a[distance=..50] run playsound minecraft:entity.evoker.prepare_attack master @s ~ ~ ~

# Spawn particles around the affected area
execute as @s run particle minecraft:enchant ~ ~ ~ 1 1 1 0.1 100

# Apply Slowness
effect give @e[tag=immobilized] slowness 10 255 true

# closing sequence
schedule function lulasmod:immobilize_part2 10s