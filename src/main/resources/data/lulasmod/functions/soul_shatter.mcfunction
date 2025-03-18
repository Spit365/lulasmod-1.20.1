# Select entities within 15 blocks, excluding the player
execute at @s as @e[distance=..15,tag=!tailed,type=!item] run tag @s add soul_shattered

# Play sound effect at the player's position
execute at @e[tag=soul_shattered] run playsound minecraft:entity.player.hurt_freeze master @e[tag=soul_shattered] ~ ~ ~

# Spawn particles around the affected area
execute at @s run particle minecraft:snowflake ~ ~ ~ 1 1 1 0.1 100
execute at @e[tag=soul_shattered] run particle minecraft:snowflake ~ ~ ~ 1 1 1 0.1 100
