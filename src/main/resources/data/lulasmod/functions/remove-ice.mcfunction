#clear ice blocks
execute at @e[tag=immobilized] run fill ~-2 ~-2 ~-2 ~2 ~2 ~2 air replace packed_ice

# Clear the tag after effects have been applied
execute as @e[tag=immobilized] at @s run tag @s remove immobilized