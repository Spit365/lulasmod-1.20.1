# Select entities within 15 blocks, excluding the player
execute as @e[distance=..15,tag=!tailed,type=!item] at @s run tag @s add immobilized

# Apply slowness and weakness effects to immobilized entities
execute as @e[tag=immobilized] at @s run effect give @s minecraft:slowness 10 255 true
execute as @e[tag=immobilized] at @s run effect give @s minecraft:weakness 10 255 true
execute as @e[tag=immobilized] at @s run effect give @s minecraft:mining_fatigue 10 255 true

#spawn ice blocks around target
execute at @e[tag=immobilized] run fill ~-2 ~-2 ~-2 ~2 ~2 ~2 packed_ice replace air
execute at @e[tag=immobilized] run fill ~-2 ~-2 ~-2 ~2 ~2 ~2 packed_ice replace light
execute at @e[tag=immobilized] run fill ~-2 ~-2 ~-2 ~2 ~2 ~2 packed_ice replace cave_air
execute at @e[tag=immobilized] run fill ~-2 ~-2 ~-2 ~2 ~2 ~2 packed_ice replace void_air

# Play sound effect at the player's position
execute as @a[distance=..15] run playsound minecraft:entity.evoker.prepare_attack master @s ~ ~ ~

# Spawn particles around the affected area
execute as @s run particle minecraft:enchant ~ ~ ~ 1 1 1 0.1 100

#closing sequence
schedule function the_tailed:remove-ice 10s