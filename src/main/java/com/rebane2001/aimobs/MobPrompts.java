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

    // Class to represent the structure of the JSON file
    public static class RecentEventsContainer {
        public String[] recentEvents;
    }

    public static String[] readRecentEvents() {
        try (FileReader reader = new FileReader("recent_events.json")) {
            Gson gson = new Gson();
            RecentEventsContainer container = gson.fromJson(reader, RecentEventsContainer.class);
            return container.recentEvents;
        } catch (IOException e) {
            e.printStackTrace();
            return new String[0]; // Return an empty array if an error occurs
        }
    }
    
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
            "melancholic", "phlegmatic", "sanguine", "choleric"
        };

        String[] languageBackgrounds = {
            "gritty gangster rappers", "sinister mafia bosses", "stoned-out hippies", "rowdy Rednecks", "snobbish ancient nobility",
            "mud-slinging pig farmers", "cutthroat Wall-street sharks", "flashy pimps", "silent monks", "poppy K-Pop stars",
            "mystical alchemists", "potion-stirring apothecaries", "sharp-eyed archers", "hammering armorsmiths", "busy bakers",
            "chatty barbers", "melodious bards", "buzzing beekeepers", "red-hot blacksmiths", "frothy brewers",
            "chop-happy butchers", "waxy candlemakers", "sawdusty carpenters", "world-traveling cartographers", "grease-smearing chandlers",
            "scribbling clerks", "nimble-fingered cobblers", "barrel-rolling coopers", "twirling dancers", "shoe-fitting farriers",
            "net-casting fishermen", "arrow-feathering fletchers", "murderous gardeners", "red-cheeked glassblowers", "freezing glovemakers",
            "greedy goldsmiths", "trumpeting heralds", "shadow-shy illuminators", "gem-loving jewelers", "juggling jugglers",
            "beef-loving leatherworkers", "stone-laying masons", "haggling merchants", "millstone-grinding millers", "song-singing minstrels",
            "midjourney painters", "clay-shaping potters", "chisel-wielding sculptors", "leaky shipbuilders", "siege-crafting engineers"
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
        if (intelligence == 0) return "and have always been extremely simple minded. ";
        if (intelligence == 1) return "and always struggled to understand simple things. ";
        if (intelligence == 2) return "and you have always been smart but not book-smart. ";
        if (intelligence == 3) return "and you were born with clear signs of intelligence. ";
        return "and achieved great wisdom and intellect. ";
    }

    public static String happinessPrompt(Entity entity) {
        int happiness = getMobStats(entity.getUuid()).getHappiness();
        if (happiness == 0) return "You have always been very sad ";
        if (happiness == 1) return "You are presently sad ";
        if (happiness == 2) return "At the moment you are happy ";
        if (happiness == 3) return "Today your eyes sparkle with happiness ";
        return "You woke up ecstatic and full of joy ";
    }

    public static String hungryPrompt(Entity entity) {
        int hunger = getMobStats(entity.getUuid()).getHunger();
        if (hunger == 0) return "You are starving, ";
        if (hunger == 1) return "You are very hungry, searching for something to eat, ";
        if (hunger == 2) return "You are hungry, ";
        if (hunger == 3) return "You have a slight appetite, ";
        return "Your belly is full, ";
    }

    public static String likesPlayerPrompt(Entity entity) {
        int love = getMobStats(entity.getUuid()).getLikesPlayer();
        if (love == 0) return "Your eyes blaze with hatred towards the adventurer. ";
        if (love == 1) return "You glare at the adventurer, showing clear signs of dislike. ";
        if (love == 2) return "You look at the adventurer with a slight fondness. ";
        if (love == 3) return "You gaze at the adventurer warmly. ";
        return "You gaze at the adventurer lovingly, your eyes filled with affection. ";
    }

    public static String isAttractedByPlayerPrompt(Entity entity) {
        int attraction = getMobStats(entity.getUuid()).getIsAttractedByPlayer();
        if (attraction == 0) return "and they seem totally unattractive to you. ";
        if (attraction == 1) return "and you are midly attracted. ";
        if (attraction == 2) return "and you take note of their physical appearance with a mild sexual tension. ";
        if (attraction == 3) return "and yet you show a clear interest in their physical appearance. ";
        return "and yet you can't take your horny eyes off them. ";
    }

    public static String personalityPrompt(Entity entity) {
        String personality = getMobStats(entity.getUuid()).getPersonality();
        return personality;
    }

    public static String languageBackgroundPrompt(Entity entity) {
        String languageBackground = getMobStats(entity.getUuid()).getLanguageBackground();
        return "And you talk like you grew up among " + languageBackground + ". ";
    }

    // Mob Stat Methods (non random)
    public static String healthyPrompt(Entity entity) {
        float healthRatio = entity instanceof LivingEntity ? ((LivingEntity) entity).getHealth() / ((LivingEntity) entity).getMaxHealth() : 0;
        return "your physique is " + (healthRatio > 0.5 ? "strong and vigorous" : "weak and frail") + ". \n\n";
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

    // Method to retrieve a random recent event prompt
    public static String randomRecentEventPrompt() {
        String recentEvent = recentEvents[(int) (Math.random() * recentEvents.length)];
        return recentEvent;
    }

    // Method to combine all mob-related prompts
    public static String createMobPrompt(Entity entity, PlayerEntity player) {
        StringBuilder prompt = new StringBuilder();
        //prompt.append(randomRecentEventPrompt());
        prompt.append(happinessPrompt(entity));               // Mob's happiness
        prompt.append(intelligencePrompt(entity));            // Mob's intelligence
        //prompt.append(personalityPrompt(entity));             // Mob's personality
        prompt.append(hungryPrompt(entity));                  // Mob's hunger
        prompt.append(healthyPrompt(entity));                 // Mob's health
        prompt.append(languageBackgroundPrompt(entity));      // Mob's language background
        prompt.append(likesPlayerPrompt(entity));        // Mob's love or hates player
        prompt.append(isAttractedByPlayerPrompt(entity));     // Mob's attraction towards player
        if (entity instanceof VillagerEntity villager) {      // Players reputation if it's a villager
            prompt.append(reputationPrompt(villager, player));
        }
        return prompt.toString();
        //prompt.append(improviseDialogue(entity, player.getDisplayName().getString()));
        //prompt.append(roleScriptPrompt(entity));
        
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

    // Recent Events
    public static String[] recentEvents = {
        "Yesterday, you witnessed a jousting tournament where the local champion was unseated by a mysterious knight. ",
        "Last week, you stumbled upon a hidden treasure while digging in the fields, but a cunning wizard claimed it. ",
        "Last night, you were part of a village feast celebrating the harvest, filled with music, dance, and laughter. ",
        "Just this morning, you heard rumors of a dragon seen flying over the distant mountains, spreading fear among the villagers. ",
        "Two days ago, you helped in constructing a new bridge that will connect two rival kingdoms, fostering trade and peace. ",
        "Last month, you were caught in a thunderstorm while traveling and took shelter in an ancient, eerie castle. ",
        "A fortnight ago, you overheard a secret meeting between nobles plotting against the king, but dare not speak of it. ",
        "Recently, you found a wounded fairy in the forest and helped it recover, earning a mystical blessing. ",
        "Three days ago, you were chased by a pack of wild wolves but managed to escape by climbing a tall tree. ",
        "Earlier today, you assisted a group of monks in copying sacred texts, learning wisdom and patience. ",
        "Last Sunday, you helped a lost child find their way back home, earning gratitude from a worried family. ",
        "A week ago, you were part of a caravan that was attacked by bandits, but your quick thinking saved the day. ",
        "Yesterday evening, you danced with a stranger at the town square, who turned out to be a disguised prince. ",
        "Just hours ago, you found an ancient scroll that hints at the location of a long-lost civilization. ",
        "Three nights ago, you had a vivid dream where a celestial being gave you a cryptic prophecy. ",
        "Last harvest, you helped save the crops from a sudden frost using a magical artifact you found. ",
        "A month ago, you were bitten by a mysterious creature in the woods and now feel a strange power growing within. ",
        "Recently, you were invited to a secret society of mages who believe you have hidden potential. ",
        "Two weeks ago, you rescued a bird tangled in a thorn bush, only to discover it could speak human language. ",
        "Earlier this month, you stumbled upon a hidden glade filled with glowing plants and singing fairies. ",
        "Last winter, you helped a village survive a harsh famine by sharing your stored food. ",
        "A few days ago, you discovered a hidden tunnel leading to the royal treasury but kept it a secret. ",
        "Yesterday, you saved a drowning kitten from a turbulent river, revealing your compassionate nature. ",
        "Earlier this year, you witnessed a solar eclipse that the wise elders say is an ominous sign. ",
        "A fortnight ago, you helped a wandering minstrel compose a song that's now famous in the region. ",
        "Last night, you had a strange encounter with a ghostly apparition that left you with a cryptic message. ",
        "This morning, you found a magic ring that glows faintly but have yet to discover its powers. ",
        "A week ago, you were part of a hunting party that caught a legendary beast, celebrated in local folklore. ",
        "Three days ago, you traded stories with a traveling merchant who shared secrets of distant lands. ",
        "Recently, you stumbled upon a mystical stone circle that resonates with ancient energy. ",
        "Last month, you helped defend the town from marauding orcs, becoming a local hero. ",
        "Yesterday, you discovered a rare herb that is said to cure a deadly disease spreading in the kingdom. ",
        "Last summer, you took part in a grand festival where you won a dance competition. ",
        "Two days ago, you were visited by a time-traveling wizard who showed you glimpses of possible futures. ",
        "A few nights ago, you found an old diary detailing the adventures of a legendary hero related to you. ",
        "Earlier this week, you saved a caravan from sinking in quicksand, using only wits and a sturdy rope. ",
        "Recently, you encountered a mystical deer in the forest that led you to a hidden waterfall. ",
        "Last spring, you planted a strange seed that grew into a tree bearing golden fruit. ",
        "A month ago, you solved an ancient riddle that unlocked a secret chamber in the old castle. ",
        "Yesterday, you met a shapeshifter who taught you valuable lessons in adaptability and change. ",
        "Last evening, you attended a royal banquet where you danced with nobles and royalty. ",
        "Three weeks ago, you were blessed by a wandering sage who saw great potential in you. ",
        "Earlier this year, you helped a group of dwarves recover a lost treasure from a dragon's lair. ",
        "Last winter, you survived a terrible blizzard by finding shelter in a cave filled with ancient carvings. ",
        "Yesterday, you came across a wandering minstrel who shared tales of distant lands and forgotten lore. ",
        "A fortnight ago, you found a magical ring that grants you glimpses of the future but at a mysterious cost. ",
        "Three days back, you encountered a ghostly apparition near the old ruins, begging for release from a curse. ",
        "Recently, you were given a map by a dying soldier, pointing to a hidden cache of the king's gold. ",
        "Last summer, you joined a group of adventurers on a perilous quest, only to be betrayed by one of them. ",
        "Earlier this week, you helped a witch gather rare herbs and were gifted a potion of unknown effects. ",
        "Just this morning, you spotted a comet streaking across the sky, an omen believed to foretell great change. ",
        "A week ago, you were saved from a deadly trap by a mysterious stranger who vanished without a trace. ",
        "Last full moon, you felt an inexplicable urge to wander into the forest, where you discovered a hidden shrine. ",
        "Two months ago, you were part of a failed rebellion and had to flee for your life, living in hiding since. ",
        "A year ago, you witnessed a duel between two wizards that left a part of the forest permanently enchanted. ",
        "Recently, you took part in a royal feast, where you accidentally uncovered a plot to poison the king. ",
        "Last night, you dreamt of a beautiful siren calling you from the sea, her song still echoing in your ears. ",
        "Three weeks ago, you joined a sea voyage that ended in shipwreck, leaving you stranded on a mystical island. ",
        "Last spring, you aided in a ritual that brought rain to a drought-stricken village, but at a personal cost. ",
        "Just yesterday, you found a diary belonging to your ancestor, revealing secrets about your family's past. ",
        "Earlier today, you were challenged to a duel by a scorned noble, who underestimated your fighting skill. ",
        "Five days ago, you were granted audience with the queen, who tasked you with a secret mission. ",
        "Last harvest moon, you stumbled upon a haunted graveyard where the dead were seen walking. ",
        "Two nights ago, you were awakened by a phantom whispering riddles that seem to hold some hidden truth. ",
        "A month ago, you helped an injured unicorn in the woods, earning the favor of the woodland creatures. ",
        "Last autumn, you participated in a tournament and bested a knight, earning a noble title. ",
        "Recently, you were approached by a talking cat who led you to a hidden world of magical beings. ",
        "Yesterday, you discovered a mirror that shows not your reflection but a parallel world. ",
        "Last week, you aided a group of dwarves in their mine, unearthing a vein of precious gems. ",
        "Hours ago, you heard a prophecy from a mad seer who spoke of your role in an epic destiny. ",
        "Days ago, you were captured by trolls but managed to escape through clever negotiation. ",
        "Last spring, you found an egg that hatched into a baby dragon, now hidden in your barn. ",
        "Just last night, you took part in a secret meeting of rebels planning to overthrow the tyrant king. ",
        "A year ago, you fell in love with a fairy who visits you every midsummer's night. ",
        "Recently, you were cursed by a vengeful sorceress, and now seek a way to break the spell. ",
        "Last month, you climbed the tallest mountain, discovering the entrance to a hidden temple. ",
        "Earlier today, you traded with a goblin merchant, acquiring a bag that never seems to empty. ",
        "Last festival, you won a dance with the princess, but she seems to have mistaken you for someone else. ",
        "Yesterday, you were attacked by a swarm of enchanted bees guarding a wizard's hidden garden. ",
        "A week ago, you discovered a well that answers any one question truthfully, but only once. ",
        "Last winter, you saved a town from starvation by leading them to a valley of wild game. ",
        "Recently, you were knighted by a legendary hero who sees great potential in you. ",
        "Two days ago, you spoke to a tree that shared wisdom from centuries of watching the world. ",
        "Last summer, you swam in a magical spring that healed your wounds but changed your appearance. ",
        "Earlier this month, you encountered a time-traveler who showed you possible futures. ",
        "Three nights ago, you were gifted a sword by a lake spirit, claiming you were the chosen one. ",
        "A fortnight ago, you were trapped in a wizard's labyrinth but found your way out through wit. ",
        "Last equinox, you joined a druid's circle in a ritual that aligned the energies of the land. ",
        "Just yesterday, you rescued a prince disguised as a commoner from a band of highwaymen. ",
        "Earlier today, you found a locket that shows the face of the person who loves you most. ",
        "Last night, you traded with a wandering villager, acquiring a rare enchanted book with unknown powers. ",
        "A week ago, you stumbled upon an Illager patrol and barely escaped with your life, their banner still in your possession. ",
        "Yesterday, you helped a group of villagers fend off a zombie siege, earning their respect and gratitude. ",
        "Recently, you mined into a cavern filled with glowing mushrooms and heard whispers of an ancient civilization. ",
        "Just this morning, you discovered a hidden stronghold, feeling the presence of the Ender Dragon watching from afar. ",
        "Two days ago, you rescued a wolf from a perilous trap, and it has loyally followed you ever since. ",
        "Last month, you explored a Jungle Temple, where you solved a complex puzzle that revealed hidden treasure. ",
        "A fortnight ago, you witnessed a clash between an Iron Golem and a group of Pillagers, learning from their tactics. ",
        "Three days ago, you were caught in a thunderstorm and saw a skeleton horse trap for the first time. ",
        "Earlier today, you fished a mysterious map from the ocean, pointing to a buried treasure guarded by Drowned. ",
        "Last winter, you ventured into the Ice Spikes biome and found a rare Ice Queen who granted you a magical boon. ",
        "A year ago, you tamed a wild ocelot in the jungle, and it has become your steadfast companion. ",
        "Last harvest, you helped a farmer villager expand his crops, learning new farming techniques and recipes. ",
        "Recently, you delved into a Nether fortress, battling Blazes and Withers, and returning with valuable resources. ",
        "A week ago, you witnessed a baby turtle hatch on the beach, feeling a connection to the cycle of life. ",
        "Last summer, you participated in a Pig race at a village fair, winning a golden carrot as a prize. ",
        "Yesterday, you saved a villager from a Witch's curse, using a potion brewed from rare herbs. ",
        "Three weeks ago, you explored a haunted Mansion, defeating the Illagers and claiming it as your stronghold. ",
        "Last full moon, you heard the distant howl of a phantom, a chilling reminder of your need for rest. ",
        "Two months ago, you befriended a Snow Golem and helped it find a suitable snowy home. ",
        "Last autumn, you were led by a parrot to a hidden pirate shipwreck, filled with doubloons and emeralds. ",
        "Earlier this week, you were challenged to a build-off by an expert builder villager, honing your construction skills. ",
        "Five days ago, you entered the End and were awed by the Endermen, learning to navigate their strange ways. ",
        "Just yesterday, you negotiated peace between a village and a Pillager outpost, fostering a tentative truce. ",
        "Last spring, you discovered a secret society of Redstone engineers, learning complex mechanisms from them. ",
        "A month ago, you survived a cave filled with Bats and found a hidden chamber with a Lapis Lazuli deposit. ",
        "Recently, you tamed a fierce Ravager left behind from a raid, and now ride it into battle. ",
        "Last night, you shared a campfire with a group of travelers, exchanging tales of adventure and rare recipes. ",
        "Two nights ago, you spotted a Shulker hiding in your storeroom, leading to a thrilling chase and capture. ",
        "Last winter, you followed a trail of strange particles to a hidden Enchanting Room with ancient spells. ",
        "A year ago, you saved a Beached Squid, earning the favor of the ocean's mysterious spirits. ",
        "Last festival, you danced with the villagers around a Maypole, feeling a connection to their simple joys. ",
        "Yesterday, you assisted a blacksmith villager in forging a special sword, imbued with a secret enchantment. ",
        "Last week, you discovered a village in the clouds, accessed only by a hidden portal in the mountains. ",
        "Hours ago, you were approached by a fox carrying a magical berry, leading you to an enchanted glade. ",
        "Days ago, you explored an underwater ruin, battling Guardians and discovering a forgotten king's crown. ",
        "Last spring, you planted a magical sapling that grew instantly into a towering tree with golden apples. ",
        "Just last night, you deciphered an ancient glyph in a desert temple, unlocking the way to a Pharaoh's tomb. ",
        "A year ago, you saved a village from a rampaging Ender Dragon, using only your wits and a fishing rod. ",
        "Recently, you sailed across a vast ocean, discovering new lands and mapping uncharted territories. ",
        "Last equinox, you attended a gathering of all the villagers, where you were honored as a hero of the realm. ",
        "Yesterday, you discovered a hidden garden filled with every kind of flower, tended by a peaceful Beekeeper. ",
        "Last night, you were visited in a dream by a Mooshroom, guiding you to a mystical island. ",
        "A week ago, you unlocked a secret level in a dungeon, battling Silverfish and finding a legendary artifact. ",
        "Last winter, you wrote a book detailing your adventures, and it's now a best-seller among the villagers. ",
        "Recently, you crafted a unique banner that became the symbol of unity for a once divided village. "
    };

}
