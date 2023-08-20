package com.rebane2001.aimobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import java.util.Locale;
import java.util.Optional;

public class PromptManager {
    
    public static boolean isEntityHurt(LivingEntity entity) {
        return entity.getHealth() * 1.2 < entity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    public static String createPromptVillager(VillagerEntity villager, PlayerEntity player, String entityName) {
        boolean isHurt = isEntityHurt(villager);
        String villageName = villager.getVillagerData().getType().toString().toLowerCase(Locale.ROOT) + " village";
        int rep = villager.getReputation(player);
        if (rep < -5) villageName = villageName + " that sees you as horrible";
        if (rep > 5) villageName = villageName + " that sees you as reputable";
        if (villager.isBaby()) {
            entityName = "Villager Kid";
            return String.format("You see a kid in a %s. The kid shouts: \"", villageName);
        }
        String profession = StringUtils.capitalize(villager.getVillagerData().getProfession().toString().toLowerCase(Locale.ROOT).replace("none", "freelancer"));
        entityName = profession;
        if (villager.getVillagerData().getLevel() >= 3) profession = "skilled " + profession;
        if (isHurt) profession = "hurt " + profession;
        return String.format("You meet a %s in a %s. The villager says to you: \"", profession, villageName);
    }

    public static String createPromptLiving(LivingEntity entity) {
        boolean isHurt = isEntityHurt(entity);
        String baseName = entity.getName().getString();
        String name = baseName;
        Text customName = entity.getCustomName();
        if (customName != null)
            name = baseName + " called " + customName.getString();
        String entityName = baseName;
        if (isHurt) name = "hurt " + name;
        String biome = getBiome(entity);
        return String.format("You meet a talking %s in the %s. The %s says to you: \"", name, biome, baseName);
    }

    public static String createPrompt(Entity entity, PlayerEntity player, String entityName) {
        if (entity instanceof VillagerEntity villager) return createPromptVillager(villager, player, entityName);
        if (entity instanceof LivingEntity entityLiving) return createPromptLiving(entityLiving);
        return "You see a " + entityName + ". The " + entityName + " says: \"";
    }
    
    private static String getBiome(Entity entity) {
        Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeAccess().getBiome(entity.getBlockPos()).getKey();
        if (biomeKey.isEmpty()) return "place";
        return Util.createTranslationKey("biome", biomeKey.get().getValue());
    }

    
}
