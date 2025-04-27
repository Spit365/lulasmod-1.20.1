#SOUL SHATTER CODE
#force soul shattered to look at me
execute as @e[tag=soul_shattered] at @s facing entity @e[tag=tailed, limit=1] feet run tp @s ~ ~ ~ ~ ~

# actually soul_shatter people
effect give @e[tag=soul_shattered] minecraft:slowness 1 255 true
effect give @e[tag=soul_shattered] minecraft:mining_fatigue 1 255 true
effect give @e[tag=soul_shattered] minecraft:weakness 1 255 true

# Clear the tag after damage has been taken
execute as @e[tag=soul_shattered,nbt={HurtTime:9s}] run tag @s remove soul_shattered