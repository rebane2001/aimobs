package com.rebane2001.aimobs;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import java.util.Map;

public class PlayerPrompts {

    public static String healthPrompt(PlayerEntity player) {
        float healthRatio = player.getHealth() / player.getMaxHealth();
        if (healthRatio == 1) return "They feel in peak condition. ";
        if (healthRatio >= 0.8) return "They feel strong and healthy. ";
        if (healthRatio >= 0.6) return "The adventurer feel good, with only minor scrapes. ";
        if (healthRatio >= 0.4) return "The adventurer feel a bit battered and bruised. ";
        if (healthRatio >= 0.2) return "The adventurer feel weak and wounded.";
        return "Their health is on the brink of collapse. ";
    }

    public static String hungerPrompt(PlayerEntity player) {
        int foodLevel = player.getHungerManager().getFoodLevel();
        if (foodLevel == 20) return "and not hungry at all. ";
        if (foodLevel >= 16) return "and slightly peckish. ";
        if (foodLevel >= 12) return "and hungry. ";
        if (foodLevel >= 8) return "and pretty hungry. ";
        if (foodLevel >= 4) return "and very hungry. ";
        return "and extremely hungry. ";
    }

    public static String experiencePrompt(PlayerEntity player) {
        int experienceLevel = player.experienceLevel;
        if (experienceLevel >= 30) return "The adventurer is reached a high age and level of expertise. ";
        if (experienceLevel >= 20) return "The adventurer is middle aged and quite skilled. ";
        if (experienceLevel >= 10) return "The adventurer is of young age with moderate level of experience. ";
        return "The adventurer is almost a child and just beginning their adventures. ";
    }


    public static String heldItemPrompt(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getCount() > 0) {
            return "In their hand, they hold a " + heldItem.getName().getString() + ", ready for action. ";
        }
        return "";
    }

    public static String potionEffectsPrompt(PlayerEntity player) {
        Map<StatusEffect, StatusEffectInstance> effects = player.getActiveStatusEffects();
        if (effects.isEmpty()) return "";
        StringBuilder prompt = new StringBuilder("Magical energies course through them, affected by ");
        for (StatusEffectInstance effect : effects.values()) {
            prompt.append(effect.getEffectType().getName()).append(", ");
        }
        prompt.setLength(prompt.length() - 2); // Remove trailing comma and space
        prompt.append(". ");
        return prompt.toString();
    }

    public static String abilitiesPrompt(PlayerEntity player) {
        // Logic to describe player's abilities
        return ""; // Placeholder
    }

    public static String statsPrompt(PlayerEntity player) {
        // Logic to describe player's stats, such as achievements and statistics
        return ""; // Placeholder
    }

    // Additional method to combine all player-related prompts
    public static String createPlayerPrompt(PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(experiencePrompt(player));
        prompt.append(healthPrompt(player));
        prompt.append(hungerPrompt(player));
        prompt.append(heldItemPrompt(player));
        //prompt.append(abilitiesPrompt(player));
        prompt.append(potionEffectsPrompt(player));
        //prompt.append(statsPrompt(player));
        return prompt.toString();
    }
}
