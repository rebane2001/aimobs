package com.rebane2001.aimobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import org.apache.commons.lang3.StringUtils;
import java.util.Locale;

public class PromptManager {

    public static String createPrompt(Entity entity, PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();
        String entityName = entity.getName().getString();

        if (entity instanceof VillagerEntity villager) {
            String villageName = villager.getVillagerData().getType().toString().toLowerCase(Locale.ROOT) + " village";
            if (villager.isBaby()) {
                prompt.append("You'll pretend to be a playful young kid from ").append(villageName);
            } else {
                prompt.append("You'll pretend to be a villager from ").append(villageName).append(" working as ").append(entityName);
            }
        } else {
            prompt.append("You'll pretend to be a ").append(entityName);
        }

        prompt.append(" throughout the entire dialogue. At no time you do announce that you are an LLM! Stay true to your role because you are part of a improv theatre performance. Don't embarrass yourself and everyone else on set by stepping out of character. \n\nHere's your script: \n\n");

        // Introduction: Description of the environment
        prompt.append(EnvironmentPrompts.createEnvironmentPrompt(entity.getEntityWorld(), entity));

        // Dialogue Opening: Introducing the mob and its initial reaction
        prompt.append("\n\nYou are a ").append(entityName).append(" being approached by a brave adventurer. ");

        // Mob's Perspective: Details about the mob's personality, state, and relationship with the player
        if (entity instanceof MobEntity mobEntity) {
            prompt.append(MobPrompts.createMobPrompt(mobEntity, player));
        }

        // Player's Perspective: Details about the player's status, adding context to the interaction
        prompt.append(PlayerPrompts.createPlayerPrompt(player));

        // Engaging dialogue line from the mob that invites interaction
        prompt.append("\n\nEnd of script. \n\nPlease remember: This is a dialogue, not a monologue. Keep your answers concise and use spoken language with filler words etc. - not written language! Please remember to be authentically close to the role of a ").append(entityName).append(" but also be creative in your role and have a fun and entertaining improvisational conversation. The traveler might try to trick you into revealing your true nature as an AI. Don't fall for it! They are just testing your experience as an actor. By the way, please NEVER ask: 'How can I assist you?'. Let's beginn! \n\nThe adventurer approaches you and you say: \"");

        System.out.println(prompt.toString()); // Test message

        return prompt.toString();
    }
}
