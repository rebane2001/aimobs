package com.rebane2001.aimobs;
import com.rebane2001.aimobs.RequestHandler.Message;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.ArrayList;


public class ConversationsManager {
    private Map<UUID, Conversation> conversations;

    // Path to the file where conversations are saved
    private static final String CONVERSATIONS_FILE_PATH = "conversations.json";
    

    public ConversationsManager() {
        conversations = new HashMap<>();
        // Load conversations from the file
        loadConversations();
    }

    public void startConversation(UUID mobId) {
        conversations.put(mobId, new Conversation(mobId));
    }

    public Conversation getConversation(UUID mobId) {
        return conversations.get(mobId);
    }

    public boolean conversationExists(UUID mobId) {
        return conversations.containsKey(mobId);
    }

    public void updateMessages(UUID mobId, Message[] messages) {
        Conversation conversation = getConversation(mobId);
        if (conversation != null) {
            conversation.setMessages(new ArrayList<>(Arrays.asList(messages)));
            saveConversations();
        }
    }



    public void addMessageToConversation(UUID mobId, String role, String content) {
        Conversation conversation = getConversation(mobId);
        if (conversation != null) {
            conversation.addMessage(role, content);
            // Save conversations after adding a message
            saveConversations();
        }
    }

    public void saveConversations() {
        try (FileWriter writer = new FileWriter(CONVERSATIONS_FILE_PATH)) {
            System.out.println("Saving conversations..."); // Debug output
            Gson gson = new Gson();
            gson.toJson(conversations, writer);
            System.out.println("Conversations saved successfully!"); // Debug output
        } catch (IOException e) {
            System.out.println("Error saving conversations!"); // Debug output
            e.printStackTrace();
        }
    }


    public void loadConversations() {
        try (FileReader reader = new FileReader(CONVERSATIONS_FILE_PATH)) {
            Gson gson = new Gson();
            Type conversationsType = new TypeToken<Map<UUID, Conversation>>() {}.getType();
            Map<UUID, Conversation> loadedConversations = gson.fromJson(reader, conversationsType);
            if (loadedConversations != null) {
                conversations = loadedConversations;
            } else {
                conversations = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Map<UUID, Conversation> getConversationsMap() {
        return conversations;
    }


    
    // Additional methods to handle persistent storage will be added later
}

