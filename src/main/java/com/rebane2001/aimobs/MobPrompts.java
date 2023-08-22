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
        mobStats.setInLoveWithPlayer((int) (Math.random() * 5));
        mobStats.setHatesPlayer((int) (Math.random() * 5));
        mobStats.setIsAttractedByPlayer((int) (Math.random() * 5));
        // TODO: add age of mob

        String[] personality = {
            "Melancholic", "Phlegmatic", "Sanguine", "Choleric"
        };

        String[] languageBackgrounds = {
            "gangster rap", "mafia", "stoned Hippies", "Redneck", "British nobility",
            "pig farmers", "Wall-street", "red light milieu", "monastery", "K-Pop"
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
        if (intelligence == 0) return "Its eyes seem vacant, displaying no sign of intelligence.";
        if (intelligence == 1) return "It looks confused and struggles to understand simple things.";
        if (intelligence == 2) return "It looks around curiously but seems limited in understanding.";
        if (intelligence == 3) return "It observes its surroundings with moderate intelligence.";
        return "Its eyes glint with wisdom, showing clear signs of intelligence.";
    }

    public static String happinessPrompt(Entity entity) {
        int happiness = getMobStats(entity.getUuid()).getHappiness();
        if (happiness == 0) return "Its eyes are filled with sorrow and despair.";
        if (happiness == 1) return "It looks sad and in need of comfort.";
        if (happiness == 2) return "It looks content but not particularly joyful.";
        if (happiness == 3) return "Its eyes sparkle with happiness.";
        return "It seems ecstatic and full of joy.";
    }

    public static String hungryPrompt(Entity entity) {
        int hunger = getMobStats(entity.getUuid()).getHunger();
        if (hunger == 0) return "Its ribs are showing, and it looks starving.";
        if (hunger == 1) return "It seems very hungry, searching for something to eat.";
        if (hunger == 2) return "It appears hungry but not desperate for food.";
        if (hunger == 3) return "It seems to have a slight appetite.";
        return "Its belly looks full, and it seems satisfied with its last meal.";
    }

    public static String inLoveWithPlayerPrompt(Entity entity) {
        int love = getMobStats(entity.getUuid()).getInLoveWithPlayer();
        if (love == 0) return "It seems completely indifferent to your presence.";
        if (love == 1) return "It looks at you with a very slight fondness.";
        if (love == 2) return "It looks at you with a slight fondness.";
        if (love == 3) return "It gazes at you warmly.";
        return "It gazes at you lovingly, its eyes filled with affection.";
    }

    public static String hatesPlayerPrompt(Entity entity) {
        int hate = getMobStats(entity.getUuid()).getHatesPlayer();
        if (hate == 0) return "It seems completely indifferent to you.";
        if (hate == 1) return "Its eyes narrow very slightly, showing a hint of dislike.";
        if (hate == 2) return "Its eyes narrow slightly, displaying mild dislike.";
        if (hate == 3) return "It glares at you, showing clear signs of dislike.";
        return "Its eyes blaze with hatred towards you.";
    }

    public static String isAttractedByPlayerPrompt(Entity entity) {
        int attraction = getMobStats(entity.getUuid()).getIsAttractedByPlayer();
        if (attraction == 0) return "It doesn't seem to notice your physical presence at all.";
        if (attraction == 1) return "It takes a quick glance at you but appears indifferent to your appearance.";
        if (attraction == 2) return "It seems to take note of your physical appearance with a mild curiosity.";
        if (attraction == 3) return "It frequently observes you, showing a clear interest in your physical appearance.";
        return "It can't seem to take its eyes off you, obviously drawn to your physical presence.";
    }

    public static String personalityPrompt(Entity entity) {
        String personality = getMobStats(entity.getUuid()).getPersonality();
        return "Its behavior reveals a " + personality + " personality. ";
    }

    public static String languageBackgroundPrompt(Entity entity) {
        String languageBackground = getMobStats(entity.getUuid()).getLanguageBackground();
        return "You can clearly hear that it comes from a " + languageBackground + " background.";
    }

    // Mob Stat Methods (non random)
    public static String healthyPrompt(Entity entity) {
        float healthRatio = entity instanceof LivingEntity ? ((LivingEntity) entity).getHealth() / ((LivingEntity) entity).getMaxHealth() : 0;
        return "Its physique appears " + (healthRatio > 0.5 ? "strong and vigorous" : "weak and frail") + ". ";
    }

    public static String reputationPrompt(VillagerEntity villager, PlayerEntity player) {
        int reputation = villager.getReputation(player);
        String reputationStatus = reputation > 5 ? "reputable" : reputation < -5 ? "shady" : "neutral";
        return "Among the villagers, you are seen as " + reputationStatus + ", with a reputation score of " + reputation + ". ";
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
        prompt.append(intelligencePrompt(entity));            // Mob's intelligence
        prompt.append(happinessPrompt(entity));               // Mob's happiness
        prompt.append(hungryPrompt(entity));                  // Mob's hunger
        prompt.append(inLoveWithPlayerPrompt(entity));        // Mob's love towards player
        prompt.append(hatesPlayerPrompt(entity));             // Mob's hate towards player
        prompt.append(isAttractedByPlayerPrompt(entity));     // Mob's attraction towards player
        prompt.append(personalityPrompt(entity));             // Mob's personality
        prompt.append(languageBackgroundPrompt(entity));      // Mob's language background
        prompt.append(healthyPrompt(entity));                 // Mob's health
        if (entity instanceof VillagerEntity villager) {      // Mob's reputation if it's a villager
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
        private int inLoveWithPlayer;
        private int hatesPlayer;
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
        public void setInLoveWithPlayer(int inLoveWithPlayer) {
            this.inLoveWithPlayer = inLoveWithPlayer;
        }
        public void setHatesPlayer(int hatesPlayer) {
            this.hatesPlayer = hatesPlayer;
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
        public int getInLoveWithPlayer() {
            return inLoveWithPlayer;
        }
        public int getHatesPlayer() {
            return hatesPlayer;
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
