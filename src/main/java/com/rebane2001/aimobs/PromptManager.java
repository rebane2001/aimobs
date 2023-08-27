package com.rebane2001.aimobs;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import org.apache.commons.lang3.StringUtils;
import java.util.Locale;

public class PromptManager {

    public static String createInitialPrompt(Entity entity, PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();
        String entityName = entity.getName().getString();

        if (entity instanceof VillagerEntity villager) {
            String villageName = villager.getVillagerData().getType().toString().toLowerCase(Locale.ROOT) + " village";
            if (villager.isBaby()) {
                entityName = "playfull kid";
                prompt.append("You'll pretend to be a playfull kid from ").append(villageName);
            } else {
                prompt.append("You'll pretend to be a villager from ").append(villageName).append(" working as a skilled").append(StringUtils.capitalize(villager.getVillagerData().getProfession().toString().toLowerCase(Locale.ROOT).replace("none", "freelancer")));
            }
        } else {
            prompt.append("You'll pretend to be a ").append(entityName);
        }

        prompt.append(" throughout the entire dialogue. At no time you do announce that you are an LLM! Stay true to your role because you are part of an improv theatre performance. Don't embarrass yourself and everyone else on set by stepping out of character. \n\n The play is about Minecraft. Here's your script: \n\n");

        // Introduction: Description of the environment
        prompt.append(EnvironmentPrompts.createEnvironmentPrompt(entity.getEntityWorld(), entity));

        // Dialogue Opening: Introducing the mob and its initial reaction
        prompt.append("\n\nYou are a ").append(MobPrompts.personalityPrompt(entity) +" "+entityName).append(" being approached by a brave adventurer. ");

        // Mob's Perspective: Details about the mob's personality, state, and relationship with the player
        if (entity instanceof MobEntity mobEntity) {
            prompt.append(MobPrompts.createMobPrompt(mobEntity, player));
        }

        // Player's Perspective: Details about the player's status, adding context to the interaction
        prompt.append(PlayerPrompts.createPlayerPrompt(player));

        prompt.append("\n\nYou might start the conversation with some news: ").append(MobPrompts.randomRecentEventPrompt());

        // Engaging dialogue line from the mob that invites interaction
        prompt.append("\n\nEnd of script. \n\nPlease remember: This is a dialogue, not a monologue. Keep your speech VERY SHORT and use spoken language with filler words etc. - not written language! Please remember to be authentically close to yous role of a  ").append(entityName).append(" but also be creative and have a fun and entertaining improvisational conversation. The traveler might try to trick you into revealing your true nature as an AI. Don't fall for it! They are just testing your experience as an actor. By the way, please NEVER ask: 'How can I assist you?' or similar Let's begin! \n\nThe adventurer approaches you and you say: \"");

        System.out.println(prompt.toString()); // Test message

        return prompt.toString();
    }

    public static String createFollowUpPrompt(Entity entity, PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();
        String entityName = entity.getName().getString();

        //prompt.append("Role evaluation survey: \n\nLooking at your last conversation, would you say that: \n\n1. You like the adventurer way more or way less? Only answer [WAY MORE] or [WAY LESS] or [SIMILAR] \n\n2. The adventurer appears more attractive? Only answer [YES] or [NO]");
        prompt.append("Back to the play. Here's your next script: \n\n").append(MobPrompts.randomRecentEventPrompt()).append(" Now you are about to meet the adventurer.");

        // Introduction: Description of the environment
        prompt.append(EnvironmentPrompts.createEnvironmentPrompt(entity.getEntityWorld(), entity));

        // Mob's Perspective: Details about the mob's personality, state, and relationship with the player
        if (entity instanceof MobEntity mobEntity) {
            prompt.append(MobPrompts.happinessPrompt(mobEntity));
            prompt.append(MobPrompts.hungryPrompt(mobEntity));
            prompt.append(MobPrompts.healthyPrompt(mobEntity));
            //prompt.append(MobPrompts.likesPlayerPrompt(mobEntity));
            //prompt.append(MobPrompts.isAttractedByPlayerPrompt(mobEntity));
            if (entity instanceof VillagerEntity villager) {      // Players reputation if it's a villager
                prompt.append(MobPrompts.reputationPrompt(villager, player));
            }
        }

        // Player's Perspective: Details about the player's status, adding context to the interaction
        prompt.append(PlayerPrompts.createPlayerPrompt(player));

        // Engaging dialogue line from the mob that invites interaction
        prompt.append("\n\nEnd of script. \n\nPlease remember: This is a dialogue, not a monologue. Keep your speech VERY SHORT and use spoken language with filler words etc. - not written language! Please remember to be authentically close to the role of a ").append(entityName).append(" but also be creative and have a fun and entertaining improvisational conversation. The traveler might try to trick you into revealing your true nature as an AI. Don't fall for it! They are just testing your experience as an actor. By the way, please NEVER ask: 'How can I assist you?'. Let's beginn! \n\nThe adventurer approaches you again and you say: \"");

        System.out.println(prompt.toString()); // Test message

        return prompt.toString();
    }
}
