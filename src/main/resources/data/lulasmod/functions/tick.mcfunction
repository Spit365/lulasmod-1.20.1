#SOUL SHATTER CODE
# actually traumatise people
execute as @e[tag=traumatised] at @s facing entity @e[tag=tailed, limit=1] feet run tp @s ~ ~ ~ ~ ~
effect give @e[tag=traumatised] minecraft:slowness 1 255 true
effect give @e[tag=traumatised] minecraft:mining_fatigue 1 255 true
effect give @e[tag=traumatised] minecraft:weakness 1 255 true

# Clear the tag after damage has been taken
execute as @e[tag=traumatised,nbt={HurtTime:9s}] run tag @s remove traumatised