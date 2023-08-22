package com.rebane2001.aimobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;




public class MobPrompts {
    
    private static final String MOB_STATS_JSON_FILE = "mob_stats.json"; // Path to the JSON file containing mob stats

    // Method to retrieve the mob stats for a given UUID
    private static MobStats getMobStats(UUID uuid) {
        Map<String, MobStats> mobStatsMap;
        try (JsonReader jsonReader = new JsonReader(new FileReader(MOB_STATS_JSON_FILE))) {
            Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
            Type type = new TypeToken<Map<String, MobStats>>(){}.getType();
            mobStatsMap = gson.fromJson(jsonReader, type);
        } catch (IOException e) {
            e.printStackTrace();
            mobStatsMap = new HashMap<>(); // Initialize with empty map if an error occurs
        }

        MobStats stats = mobStatsMap.get(uuid.toString());
        if (stats == null) {
            stats = createDefaultStats();
            mobStatsMap.put(uuid.toString(), stats); // Add the new stats to the map
            try (FileWriter fileWriter = new FileWriter(MOB_STATS_JSON_FILE)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(mobStatsMap, fileWriter); // Write the updated map to the file
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stats;
    }



    // Method to update the defauld mob stats for a given UUID
    public static MobStats createDefaultStats() {
        MobStats mobStats = new MobStats();
        mobStats.setIntelligence((int) (Math.random() * 5));
        mobStats.setHappiness((int) (Math.random() * 5));
        mobStats.setHunger((int) (Math.random() * 5));
        mobStats.setLikesPlayer((int) (Math.random() * 5));
        mobStats.setIsAttractedByPlayer((int) (Math.random() * 5));
        // TODO: add age of mob

        String[] personality = {
            "Melancholic", "Phlegmatic", "Sanguine", "Choleric"
        };

        String[] languageBackgrounds = {
            "gangster rappers", "mafia bosses", "stoned hippies", "Rednecks", "British nobility",
            "pig farmers", "Wall-street sharks", "pimps and prostitutes", "monks", "K-Pop stars"
        };
        mobStats.setLanguageBackground(languageBackgrounds[(int) (Math.random() * languageBackgrounds.length)]);
        mobStats.setPersonality(personality[(int) (Math.random() * personality.length)]);

        return mobStats;
    }

    // Method to initialize the mob stats file
    public static void initializeMobStatsFile() {
        // Check if the file exists and is not empty
        if (Files.exists(Paths.get(MOB_STATS_JSON_FILE)) && !isFileEmpty(MOB_STATS_JSON_FILE)) {
            return;
        }

        // Create default mob stats
        Map<String, MobStats> defaultMobStats = new HashMap<>();
        // Example: Add default stats for specific UUIDs
        defaultMobStats.put(UUID.randomUUID().toString(), createDefaultStats());

        // Write to JSON file
        try (FileWriter fileWriter = new FileWriter(MOB_STATS_JSON_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(defaultMobStats, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static boolean isFileEmpty(String filePath) {
        try {
            return Files.size(Paths.get(filePath)) == 0;
        } catch (IOException e) {
            return true;
        }
    }
    // Mob Stats
    public static String intelligencePrompt(Entity entity) {
        int intelligence = getMobStats(entity.getUuid()).getIntelligence();
        if (intelligence == 0) return "Your eyes seem vacant, displaying no sign of intelligence. ";
        if (intelligence == 1) return "You look confused and struggle to understand simple things. ";
        if (intelligence == 2) return "You look around curiously but seem limited in understanding. ";
        if (intelligence == 3) return "You observe Your surroundings with moderate intelligence. ";
        return "Your eyes glint with wisdom, showing clear signs of intelligence. ";
    }

    public static String happinessPrompt(Entity entity) {
        int happiness = getMobStats(entity.getUuid()).getHappiness();
        if (happiness == 0) return "Your eyes are filled with sorrow and despair ";
        if (happiness == 1) return "You look sad and in need of comfort ";
        if (happiness == 2) return "You look content but not particularly joyful ";
        if (happiness == 3) return "Your eyes sparkle with happiness ";
        return "You seem ecstatic and full of joy ";
    }

    public static String hungryPrompt(Entity entity) {
        int hunger = getMobStats(entity.getUuid()).getHunger();
        if (hunger == 0) return "Your ribs are showing, and you look starving ";
        if (hunger == 1) return "You seem very hungry, searching for something to eat ";
        if (hunger == 2) return "You appear hungry but not desperate for food ";
        if (hunger == 3) return "You seem to have a slight appetite ";
        return "Your belly looks full, and you seem satisfied with your last meal ";
    }

    public static String likesPlayerPrompt(Entity entity) {
        int love = getMobStats(entity.getUuid()).getLikesPlayer();
        if (love == 0) return "Your eyes blaze with hatred towards them. ";
        if (love == 1) return "You glare at them, showing clear signs of dislike. ";
        if (love == 2) return "You look at them with a slight fondness. ";
        if (love == 3) return "You gaze at them warmly. ";
        return "You gaze at the adventurers lovingly, your eyes filled with affection. ";
    }

    public static String isAttractedByPlayerPrompt(Entity entity) {
        int attraction = getMobStats(entity.getUuid()).getIsAttractedByPlayer();
        if (attraction == 0) return "You don't notice their physical presence at all. ";
        if (attraction == 1) return "You take a quick glance at them but you are indifferent to their appearance. ";
        if (attraction == 2) return "You take note of their physical appearance with a mild curiosity. ";
        if (attraction == 3) return "You frequently observe them, showing a clear interest in their physical appearance. ";
        return "You can't take your eyes off them, obviously drawn to their physical presence. ";
    }

    public static String personalityPrompt(Entity entity) {
        String personality = getMobStats(entity.getUuid()).getPersonality();
        return "and your behavior reveals a " + personality + " personality. ";
    }

    public static String languageBackgroundPrompt(Entity entity) {
        String languageBackground = getMobStats(entity.getUuid()).getLanguageBackground();
        return "Your way of talking clearly reveals that you grew up among " + languageBackground + ". ";
    }

    // Mob Stat Methods (non random)
    public static String healthyPrompt(Entity entity) {
        float healthRatio = entity instanceof LivingEntity ? ((LivingEntity) entity).getHealth() / ((LivingEntity) entity).getMaxHealth() : 0;
        return "but your physique appears " + (healthRatio > 0.5 ? "strong and vigorous" : "weak and frail") + ". \n\n";
    }

    public static String reputationPrompt(VillagerEntity villager, PlayerEntity player) {
        int reputation = villager.getReputation(player);
        String reputationStatus = reputation > 5 ? "reputable" : reputation < -5 ? "shady" : "neutral";
        return "Among the other villagers, the adventurer is seen as " + reputationStatus +". ";
    }

    public static String improviseDialogue(Entity entity, String playerInput) {
        // Logic to improvise a dialogue based on the entity's attributes and player's input
        return ""; // Placeholder
    }

    public static String roleScriptPrompt(Entity entity) {
        // Logic to retrieve the role-playing script or guideline for the entity
        return ""; // Placeholder
    }

    public static String randomEventPrompt(Entity entity) {
        // Logic to trigger a random event involving the entity
        return ""; // Placeholder
    }

    // Method to combine all mob-related prompts
    public static String createMobPrompt(Entity entity, PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(languageBackgroundPrompt(entity));      // Mob's language background
        prompt.append(intelligencePrompt(entity));            // Mob's intelligence
        prompt.append(happinessPrompt(entity));               // Mob's happiness
        prompt.append(personalityPrompt(entity));             // Mob's personality
        prompt.append(hungryPrompt(entity));                  // Mob's hunger
        prompt.append(healthyPrompt(entity));                 // Mob's health
        prompt.append(likesPlayerPrompt(entity));        // Mob's love or hates player
        prompt.append(isAttractedByPlayerPrompt(entity));     // Mob's attraction towards player
        if (entity instanceof VillagerEntity villager) {      // Players reputation if it's a villager
            prompt.append(reputationPrompt(villager, player));
        }
        return prompt.toString();
        //prompt.append(improviseDialogue(entity, player.getDisplayName().getString()));
        //prompt.append(roleScriptPrompt(entity));
        //prompt.append(randomEventPrompt(entity));
    }




    // Custom UUID type adapter for Gson
    private static class UUIDTypeAdapter extends TypeAdapter<UUID> {
        @Override
        public void write(JsonWriter out, UUID value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }

        @Override
        public UUID read(JsonReader in) throws IOException {
            try {
                return UUID.fromString(in.nextString());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }
    // Class to represent the persistent stats for a mob

    //Attributes
    private static class MobStats {
        private int intelligence;
        private int happiness;
        private int hunger;
        private int likesPlayer;
        private int isAttractedByPlayer;
        private String languageBackground;
        private String personality;

        //Setters
        public void setIntelligence(int intelligence) {
            this.intelligence = intelligence;
        }
        public void setHappiness(int happiness) {
            this.happiness = happiness;
        }
        public void setHunger(int hunger) {
            this.hunger = hunger;
        }
        public void setLikesPlayer(int likesPlayer) {
            this.likesPlayer = likesPlayer;
        }
        public void setIsAttractedByPlayer(int isAttractedByPlayer) {
            this.isAttractedByPlayer = isAttractedByPlayer;
        }
        public void setLanguageBackground(String languageBackground) {
            this.languageBackground = languageBackground;
        }
        public void setPersonality(String personality) {
            this.personality = personality;
        }

        //Getters
        public int getIntelligence() {
            return intelligence;
        }
        public int getHappiness() {
            return happiness;
        }
        public int getHunger() {
            return hunger;
        }
        public int getLikesPlayer() {
            return likesPlayer;
        }
        public int getIsAttractedByPlayer() {
            return isAttractedByPlayer;
        }
        public String getLanguageBackground() {
            return languageBackground;
        }
        public String getPersonality() {
            return personality;
        }
    }

}
