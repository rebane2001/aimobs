package com.rebane2001.aimobs;

import com.rebane2001.aimobs.RequestHandler.Message;
import java.util.Arrays;
import com.rebane2001.aimobs.mixin.ChatHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.io.InputStream;
import java.io.IOException;
import net.minecraft.nbt.NbtCompound;



public class ActionHandler {
    public static Message[] messages = new Message[0]; // Replace prompts string with messages array
    public static MobEntity currentEntity = null;
    public static String entityName = "";
    public static UUID entityId = null;
    public static UUID initiator = null;
    public static long lastRequest = 0;
    public static long conversationEndTime = 0; // Track when the conversation should end
    // ConversationsManager to manage all conversations with entities
    public static ConversationsManager conversationsManager = AIMobsMod.conversationsManager;
    // Field to track if the "R" key is being held down
    public static boolean isRKeyPressed = false;
    // AudioRecorder to record audio from the microphone
    private static AudioRecorder audioRecorder = new AudioRecorder();

    // The waitMessage is the thing that goes '<Name> ...' before an actual response is received
    private static ChatHudLine.Visible waitMessage;
    private static List<ChatHudLine.Visible> getChatHudMessages() {
        return ((ChatHudAccessor)MinecraftClient.getInstance().inGameHud.getChatHud()).getVisibleMessages();
    }

    // Method to show the wait message
    private static void showWaitMessage(String name) {
        if (waitMessage != null) getChatHudMessages().remove(waitMessage);
        waitMessage = new ChatHudLine.Visible(MinecraftClient.getInstance().inGameHud.getTicks(), OrderedText.concat(OrderedText.styledForwardsVisitedString("<" + name + "> ", Style.EMPTY),OrderedText.styledForwardsVisitedString("...", Style.EMPTY.withColor(Formatting.GRAY))), null, true);
        getChatHudMessages().add(0, waitMessage);
    }
    private static void hideWaitMessage() {
        if (waitMessage != null) getChatHudMessages().remove(waitMessage);
        waitMessage = null;
    }

    // Method to handle the "R" key press
    public static void onRKeyPress() {
        if (entityId != null && !isRKeyPressed) { // Check if a conversation has been started
            isRKeyPressed = true;
            PlayerEntity player = MinecraftClient.getInstance().player;
            player.sendMessage(Text.of("Listening.."));
            System.out.println("Recording started"); // Test message
            audioRecorder.startRecording();
        }
    }

    // Method to handle the "R" key release
    public static void onRKeyRelease() {
        if (isRKeyPressed) {
            isRKeyPressed = false;
            PlayerEntity player = MinecraftClient.getInstance().player;
            player.sendMessage(Text.of("Stopped listening."));
            System.out.println("R key released! Stopping voice input...");

            // Create a new thread to handle the time-consuming tasks
            new Thread(() -> {
                // Check if the voice key is set in the configuration
                if (AIMobsConfig.config.voiceApiKey.length() > 0) {
                    try {
                        // Stop the recording and get the WAV input stream
                        InputStream wavInputStream = audioRecorder.stopRecording();

                        // Get the transcription from OpenAI's Whisper ASR
                        String transcription = RequestHandler.getTranscription(wavInputStream);
                        // You can now use the transcription in your conversation logic
                        // For example, send it as a reply to the entity
                        if (player != null) {
                            replyToEntity(transcription, player);
                        }

                    } catch (IOException e) {
                        System.err.println("Error transcribing audio:");
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("Voice API key is not set. Please set it using /aimobs setvoicekey <voicekey>.");
                }
            }).start(); // Start the new thread
        }
    }


    public static void startConversation(Entity entity, PlayerEntity player) {
        if (!(entity instanceof LivingEntity)) return;
        if (entity instanceof MobEntity) {
            currentEntity = (MobEntity) entity; // Store the mob entity reference
        }
        entityId = entity.getUuid();
        entityName = StringUtils.capitalize(entity.getType().getName().getString().replace("_", " "));
        initiator = player.getUuid();
        // Check if a conversation already exists for this mob
        if (conversationsManager.conversationExists(entityId)) {
            // Resume existing conversation
            Conversation existingConversation = conversationsManager.getConversation(entityId);
            messages = conversationsManager.getConversation(entityId).getMessages().toArray(new Message[0]);
            String newPrompt = "Hi, I'm back. Nice to see you again."; // You would define this method to create the new prompt
            conversationsManager.addMessageToConversation(entityId, "user", newPrompt); // Add the new user message to the conversation
            messages = existingConversation.getMessages().toArray(new RequestHandler.Message[0]); // Update messages array
            showWaitMessage(entityName);
            getResponse(player);
        } else {
            // Start a new conversation
            conversationsManager.startConversation(entityId);
            String prompt = PromptManager.createPrompt(entity, player);
            // Adding the player's message to the conversation
            conversationsManager.addMessageToConversation(entityId, "user", prompt);
            messages = new Message[] { new Message("user", prompt) }; // Initialize messages array
            showWaitMessage(entityName);
            getResponse(player);
        }
    }


    public static boolean checkConversationEnd() {
        if (entityId != null && System.currentTimeMillis() > conversationEndTime) {
            if (currentEntity != null) {
                currentEntity.getNavigation().stop(); // Stop the mob's movement
            }
            // End the conversation
            currentEntity = null; // Reset the mob entity reference
            entityId = null;
            entityName = "";
            initiator = null;
            messages = new Message[0];
            conversationEndTime = 0;
            System.out.println("Conversation ended"); // Test message
            return true;
        }
        return false;
    }



    public static void getResponse(PlayerEntity player) {
        // 1.5 second cooldown between requests
        if (lastRequest + 1500L > System.currentTimeMillis()) return;
        if (AIMobsConfig.config.apiKey.length() == 0) {
            player.sendMessage(Text.of("[AIMobs] You have not set an API key! Get one from https://beta.openai.com/account/api-keys and set it with /aimobs setkey"));
            return;
        }
        lastRequest = System.currentTimeMillis();
        // Set the time when the conversation should end (30 seconds from now)
        conversationEndTime = lastRequest + 100000L;
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

                // Trigger text-to-speech synthesis and playback
                if (AIMobsConfig.config.enabled && AIMobsConfig.config.voiceApiKey.length() > 0) { // Check if the feature is enabled
                    TextToSpeech.synthesizeAndPlay(response, entityId); // Pass the mob's UUID to the TTS method
                }

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

    public static void handlePunch(Entity entity, Entity players) {
        if (entity.getUuid() != entityId) return;
        messages = Arrays.copyOf(messages, messages.length + 1);
        messages[messages.length - 1] = new Message("user", "You punch the " + entityName + ".");
    }
}