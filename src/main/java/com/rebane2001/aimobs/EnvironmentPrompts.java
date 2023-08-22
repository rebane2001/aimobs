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
        if (timeOfDay == 0) return "Midnight reigns, and all is still. ";
        if (timeOfDay < 1000) return "The early dawn paints the sky. ";
        if (timeOfDay < 6000) return "Morning graces the land.";
        if (timeOfDay < 12000) return "Daytime bathes the world in light. ";
        if (timeOfDay < 18000) return "Evening descends, and shadows grow. ";
        return "Nightfall cloaks the world in darkness. ";
    }

    public static String biomePrompt(Entity entity) {
        Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeAccess().getBiome(entity.getBlockPos()).getKey();
        if (biomeKey.isEmpty()) return "place";
        String translationKey = Util.createTranslationKey("biome", biomeKey.get().getValue());
        // Remove the prefix "biome.minecraft."
        String biomeName = translationKey.replace("biome.minecraft.", "");
        return "The land around you breathes the essence of the " + biomeName + ". You call it home. ";
    }

    public static String lightLevelPrompt(Entity entity) {
        int lightLevel = entity.getEntityWorld().getLightLevel(entity.getBlockPos());
        if (lightLevel == 0) return "Absolute darkness surrounds you. ";
        if (lightLevel < 5) return "It's dim and shadowy here. ";
        if (lightLevel < 10) return "The area is dimly lit. ";
        if (lightLevel < 15) return "The light here is moderate. ";
        return "Bright light fills the area. ";
    }

    public static String blockStatePrompt(Entity entity) {
        BlockPos pos = entity.getBlockPos();
        BlockState blockState = entity.getEntityWorld().getBlockState(pos);
        String blockName = blockState.getBlock().getTranslationKey().replace("block.minecraft.", "");
        if (blockName.equals("air")) return "";
        return "Beneath your feet, you feel the texture of " + blockName + ". ";
    }

    public static String weatherPrompt(World world) {
        if (world.isThundering()) return "Thunder roars, and lightning cracks the sky. ";
        if (world.isRaining()) return "Rain falls, adding life to the world. ";
        return "The sky is clear, and the weather is calm. ";
    }

    public static String nearbyEntitiesPrompt(World world, Entity entity) {
        Box boundingBox = new Box(entity.getBlockPos()).expand(10); // 10-block radius
        List<Entity> nearbyEntities = world.getEntitiesByClass(Entity.class, boundingBox, e -> e != entity); // Exclude the current entity
        if (nearbyEntities.isEmpty()) return "A stillness pervades, and no other creatures are nearby. ";
        return "Life stirs around you, and you see " + nearbyEntities.size() + " other creatures nearby. ";
    }

    public static String createEnvironmentPrompt(World world, Entity entity) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(timeOfDayPrompt(world));         // Time of day
        prompt.append(biomePrompt(entity));            // Biome
        prompt.append(lightLevelPrompt(entity));       // Light level
        prompt.append(blockStatePrompt(entity));       // Block state
        prompt.append(weatherPrompt(world));           // Weather
        prompt.append(nearbyEntitiesPrompt(world, entity)); // Nearby entities
        return prompt.toString();
    }

}
