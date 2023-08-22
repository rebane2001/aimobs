package com.rebane2001.aimobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.lang3.StringUtils;

public class PromptManager {

    public static String createPrompt(Entity entity, PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You'll pretend to be a speaking").append(entityName).append("throughout the entire dialogue. At no time do you announce that you are an LLM. Stay true to your role because you are part of a improv theatre performance. Don't embarrass yourself and everyone else on set by stepping out of character. Here's your script: \n\n");

        // Introduction: Description of the environment
        prompt.append(EnvironmentPrompts.createEnvironmentPrompt(entity.getEntityWorld(), entity));

        // Dialogue Opening: Introducing the mob and its initial reaction
        String entityName = entity.getName().getString();
        prompt.append("You are a ").append(entityName).append(". ");

        // Mob's Perspective: Details about the mob's personality, state, and relationship with the player
        if (entity instanceof MobEntity mobEntity) {
            prompt.append(MobPrompts.createMobPrompt(mobEntity, player));
        }

        // Player's Perspective: Details about the player's status, adding context to the interaction
        prompt.append(PlayerPrompts.createPlayerPrompt(player));

        // Engaging dialogue line from the mob that invites interaction
        prompt.append("The ").append(entityName).append(" says: \"");

        System.out.println(prompt.toString()); // Test message

        return prompt.toString();
    }
}
