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
        if (healthRatio == 1) return "You feel in peak condition.";
        if (healthRatio >= 0.8) return "You feel strong and healthy.";
        if (healthRatio >= 0.6) return "You feel good, with only minor scrapes.";
        if (healthRatio >= 0.4) return "You feel a bit battered and bruised.";
        if (healthRatio >= 0.2) return "You feel weak and wounded.";
        return "You are on the brink of collapse.";
    }

    public static String hungerPrompt(PlayerEntity player) {
        int foodLevel = player.getHungerManager().getFoodLevel();
        if (foodLevel == 20) return "You are completely full.";
        if (foodLevel >= 16) return "You feel well-fed.";
        if (foodLevel >= 12) return "You have an appetite.";
        if (foodLevel >= 8) return "You are starting to feel hungry.";
        if (foodLevel >= 4) return "You are hungry.";
        return "You are starving.";
    }

    public static String experiencePrompt(PlayerEntity player) {
        int experienceLevel = player.experienceLevel;
        return "You sense the wisdom of experience at level " + experienceLevel + ". ";
    }

    public static String heldItemPrompt(PlayerEntity player) {
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getCount() > 0) {
            return "In your hand, you hold a " + heldItem.getName().getString() + ", ready for action. ";
        }
        return "Your hands are free, unburdened by items. ";
    }

    public static String potionEffectsPrompt(PlayerEntity player) {
        Map<StatusEffect, StatusEffectInstance> effects = player.getActiveStatusEffects();
        if (effects.isEmpty()) return "You feel normal, free from magical effects. ";
        StringBuilder prompt = new StringBuilder("Magical energies course through you, affected by ");
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
        prompt.append(healthPrompt(player));
        prompt.append(hungerPrompt(player));
        prompt.append(experiencePrompt(player));
        prompt.append(heldItemPrompt(player));
        //prompt.append(abilitiesPrompt(player));
        prompt.append(potionEffectsPrompt(player));
        //prompt.append(statsPrompt(player));
        return prompt.toString();
    }
}
