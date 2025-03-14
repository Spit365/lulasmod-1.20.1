#force immobilized to look at me
execute as @e[tag=immobilized] at @s facing entity @e[tag=tailed] eyes run tp @s ~ ~ ~ ~ ~

# actually immobilize people
effect give @e[tag=immobilized] minecraft:slowness 1 255 true
effect give @e[tag=immobilized] minecraft:mining_fatigue 1 255 true
effect give @e[tag=immobilized] minecraft:weakness 1 255 true

# Clear the tag after damage has been taken
execute as @e[tag=immobilized,nbt={HurtTime:9s}] run tag @s remove immobilized
