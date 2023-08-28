package com.rebane2001.aimobs;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.util.Util;
import java.util.List;
import java.util.Optional;

public class EnvironmentPrompts {

    public static String timeOfDayPrompt(World world) {
        long timeOfDay = world.getTimeOfDay() % 24000;
        if (timeOfDay == 0) return "Midnight reigns, and all is still, ";
        if (timeOfDay < 1000) return "The early dawn paints the sky, ";
        if (timeOfDay < 6000) return "Morning graces the land, ";
        if (timeOfDay < 12000) return "Daytime bathes the world in light, ";
        if (timeOfDay < 18000) return "Evening descends, and shadows grow, ";
        return "Nightfall cloaks the world in darkness, ";
    }

    public static String biomePrompt(Entity entity) {
        Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeAccess().getBiome(entity.getBlockPos()).getKey();
        if (biomeKey.isEmpty()) return "land";
        String translationKey = Util.createTranslationKey("biome", biomeKey.get().getValue());
        // Remove the prefix "biome.minecraft."
        String biomeName = translationKey.replace("biome.minecraft.", "");
        return "The " + biomeName + " around you is your home. ";
    }

    public static String lightLevelPrompt(Entity entity) {
        int lightLevel = entity.getEntityWorld().getLightLevel(entity.getBlockPos());
        if (lightLevel == 0) return "But in this place here is absolute darkness you don't even see ";
        if (lightLevel < 5) return "You look around but in here you can barely see ";
        if (lightLevel < 10) return "In this dim light here it's not easy to see ";
        if (lightLevel < 15) return "The visibility here is quite good and you see ";
        return "You clearly see ";
    }

    public static String blockStatePrompt(Entity entity) {
        BlockPos pos = entity.getBlockPos();
        BlockState blockState = entity.getEntityWorld().getBlockState(pos);
        String blockName = blockState.getBlock().getTranslationKey().replace("block.minecraft.", "");
        if (blockName.equals("air")) return "";
        return "You stand on " + blockName + ". ";
    }

    public static String weatherPrompt(World world) {
        if (world.isThundering()) return "thunder roars, and lightning cracks the sky. ";
        if (world.isRaining()) return "rain falls, adding life to the world. ";
        return "the sky is clear, and the weather is calm. ";
    }

    public static String nearbyEntitiesPrompt(World world, Entity entity) {
        Box boundingBox = new Box(entity.getBlockPos()).expand(10); // 10-block radius
        List<Entity> nearbyEntities = world.getEntitiesByClass(Entity.class, boundingBox, e -> e != entity); // Exclude the current entity
        if (nearbyEntities.isEmpty()) return "but there is noone else around. You are all alone. ";
        return String.valueOf(nearbyEntities.size()).replace("1", "one").replace("1", "two").replace("1", "three") + " other creatures around. ";
    }

    public static String createEnvironmentPrompt(World world, Entity entity) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(timeOfDayPrompt(world));         // Time of day
        prompt.append(weatherPrompt(world));           // Weather
        prompt.append(biomePrompt(entity));            // Biome
        prompt.append(lightLevelPrompt(entity));       // Light level
        prompt.append(blockStatePrompt(entity));       // Block state
        prompt.append(nearbyEntitiesPrompt(world, entity)); // Nearby entities
        return prompt.toString();
    }

}
