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
    private static final String CONVERSATIONS_FILE_PATH = "conversations.json"; // Path to the file where conversations are saved

    // Constructor that initializes and loads conversations
    public ConversationsManager() {
        conversations = new HashMap<>();
        loadConversations();
    }

    // Starts a new conversation for a mob
    public void startConversation(UUID mobId) {
        conversations.put(mobId, new Conversation(mobId));
    }

    // Gets a conversation for a specific mob
    public Conversation getConversation(UUID mobId) {
        return conversations.get(mobId);
    }

    // Checks if a conversation exists for a specific mob
    public boolean conversationExists(UUID mobId) {
        return conversations.containsKey(mobId);
    }

    // Updates the messages in a conversation
    public void updateMessages(UUID mobId, Message[] messages) {
        Conversation conversation = getConversation(mobId);
        if (conversation != null) {
            conversation.setMessages(new ArrayList<>(Arrays.asList(messages)));
            saveConversations();
        }
    }

    // Adds a new message to a conversation
    public void addMessageToConversation(UUID mobId, String role, String content) {
        Conversation conversation = getConversation(mobId);
        if (conversation != null) {
            conversation.addMessage(role, content);
            saveConversations();
        }
    }

    // Saves conversations to a file
    public void saveConversations() {
        try (FileWriter writer = new FileWriter(CONVERSATIONS_FILE_PATH)) {
            Gson gson = new Gson();
            gson.toJson(conversations, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads conversations from a file
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

    // Gets the map of all conversations
    public Map<UUID, Conversation> getConversationsMap() {
        return conversations;
    }
}
