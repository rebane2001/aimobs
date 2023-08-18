package com.rebane2001.aimobs;

import com.rebane2001.aimobs.RequestHandler.Message; // Import the Message class
import java.util.Arrays; // Import the Arrays class
import com.rebane2001.aimobs.mixin.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.registry.RegistryKey;
// import net.minecraft.world.EntityView;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class ActionHandler {
    public static Message[] messages = new Message[0]; // Replace prompts string with messages array
    public static String entityName = "";
    public static UUID entityId = null;
    public static UUID initiator = null;
    public static long lastRequest = 0;
    // ConversationsManager to manage all conversations with entities
    //public static ConversationsManager conversationsManager = new ConversationsManager();
    public static ConversationsManager conversationsManager = AIMobsMod.conversationsManager;


    // The waitMessage is the thing that goes '<Name> ...' before an actual response is received
    private static ChatHudLine.Visible waitMessage;
    private static List<ChatHudLine.Visible> getChatHudMessages() {
        return ((ChatHudAccessor)MinecraftClient.getInstance().inGameHud.getChatHud()).getVisibleMessages();
    }
    private static void showWaitMessage(String name) {
        if (waitMessage != null) getChatHudMessages().remove(waitMessage);
        waitMessage = new ChatHudLine.Visible(MinecraftClient.getInstance().inGameHud.getTicks(), OrderedText.concat(OrderedText.styledForwardsVisitedString("<" + name + "> ", Style.EMPTY),OrderedText.styledForwardsVisitedString("...", Style.EMPTY.withColor(Formatting.GRAY))), null, true);
        getChatHudMessages().add(0, waitMessage);
    }
    private static void hideWaitMessage() {
        if (waitMessage != null) getChatHudMessages().remove(waitMessage);
        waitMessage = null;
    }

    private static String getBiome(Entity entity) {
        Optional<RegistryKey<Biome>> biomeKey = entity.getEntityWorld().getBiomeAccess().getBiome(entity.getBlockPos()).getKey();
        if (biomeKey.isEmpty()) return "place";
        return I18n.translate(Util.createTranslationKey("biome", biomeKey.get().getValue()));
    }

    
    public static void startConversation(Entity entity, PlayerEntity player) {
    if (!(entity instanceof LivingEntity)) return;
    entityId = entity.getUuid();
    entityName = StringUtils.capitalize(entity.getType().getName().getString().replace("_", " "));
    initiator = player.getUuid();
    // Check if a conversation already exists for this mob
    if (conversationsManager.conversationExists(entityId)) {
        player.sendMessage(Text.of("conversation exist:"));
        // Resume existing conversation
        Conversation existingConversation = conversationsManager.getConversation(entityId);
        messages = conversationsManager.getConversation(entityId).getMessages ().toArray(new Message[0]);
        String newPrompt = "Hi, I'm back. Nice to see you again."; // You would define this method to create the new prompt
        conversationsManager.addMessageToConversation(entityId, "user", newPrompt); // Add the new user message to the conversation
        messages = existingConversation.getMessages().toArray(new RequestHandler.Message[0]); // Update messages array
        showWaitMessage(entityName);
        getResponse(player);
    } else {
        player.sendMessage(Text.of("new conversation:"));
        // Start a new conversation
        conversationsManager.startConversation(entityId);
        String prompt = createPrompt (entity, player);
        ItemStack heldItem = player.getMainHandStack();
        if (heldItem.getCount () > 0)
        prompt = "You are holding a " + heldItem.getName().getString() + " in your hand. " + prompt;
        // Adding the player's message to the conversation
        conversationsManager.addMessageToConversation(entityId, "user", prompt);
        messages = new Message[] { new Message("user", prompt) }; // Initialize messages array
        showWaitMessage(entityName);
        getResponse(player);
        }
    }




    public static void getResponse(PlayerEntity player) {
        // 1.5 second cooldown between requests
        if (lastRequest + 1500L > System.currentTimeMillis()) return;
        if (AIMobsConfig.config.apiKey.length() == 0) {
            player.sendMessage(Text.of("[AIMobs] You have not set an API key! Get one from https://beta.openai.com/account/api-keys and set it with /aimobs setkey"));
            return;
        }
        lastRequest = System.currentTimeMillis();
        Thread t = new Thread(() -> {
            try {
                String response = RequestHandler.getAIResponse(messages);
                player.sendMessage(Text.of("<" + entityName + "> " + response));
                
                // Adding the AI's response to the conversation
                conversationsManager.addMessageToConversation(entityId, "assistant", response);

                // Add response to messages array
                messages = Arrays.copyOf(messages, messages.length + 1);
                messages[messages.length - 1] = new Message("assistant", response);
                conversationsManager.updateMessages(entityId, messages);
                
                System.out.println(Arrays.toString(messages));
            } catch (Exception e) {
                player.sendMessage(Text.of("[AIMobs] Error getting response"));
                e.printStackTrace();
            } finally {
                hideWaitMessage();
            }
        });
        t.start();
    }


    public static void replyToEntity(String message, PlayerEntity player) {
        if (entityId == null) return;
        String prompt = (player.getUuid() == initiator) ? "You say: \"" : ("Your friend " + player.getName().getString() + " says: \"");
        prompt += message.replace("\"", "'") + "\"\n The " + entityName + " says: \"";

        // Add user message to the conversation
        conversationsManager.addMessageToConversation(entityId, "user", message);

        // Add user message to messages array for displaying the conversation
        messages = Arrays.copyOf(messages, messages.length + 1);
        messages[messages.length - 1] = new Message("user", prompt);

        getResponse(player);
    }


    private static boolean isEntityHurt(LivingEntity entity) {
        return entity.getHealth() * 1.2 < entity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
    }

    private static String createPromptVillager(VillagerEntity villager, PlayerEntity player) {
        boolean isHurt = isEntityHurt(villager);
        entityName = "Villager";
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
        entityName = baseName;
        if (isHurt) name = "hurt " + name;
        return String.format("You meet a talking %s in the %s. The %s says to you: \"", name, getBiome(entity), baseName);
    }

    public static String createPrompt(Entity entity, PlayerEntity player) {
        if (entity instanceof VillagerEntity villager) return createPromptVillager(villager, player);
        if (entity instanceof LivingEntity entityLiving) return createPromptLiving(entityLiving);
        entityName = entity.getName().getString();
        return "You see a " + entityName + ". The " + entityName + " says: \"";
    }

    /* public static void handlePunch(Entity entity, Entity player) {
        if (entity.getUuid() != entityId) return;
        messages = Arrays.copyOf(messages, messages.length + 1);
        messages[messages.length - 1] = new Message("user", "You punch the " + entityName + ".");
    } */
}