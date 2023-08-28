package com.rebane2001.aimobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {
    private UUID mobId; // The UUID of the mob associated with this conversation
    private List<RequestHandler.Message> messages; // List of messages in the conversation

    // Constructor initializes the conversation for a specific mob
    public Conversation(UUID mobId) {
        this.mobId = mobId;
        this.messages = new ArrayList<>();
    }

    // Getter for the mob's UUID
    public UUID getMobId() {
        return mobId;
    }

    // Method to add a message to the conversation
    public void addMessage(String role, String content) {
        messages.add(new RequestHandler.Message(role, content));
    }

    // Getter for the messages in the conversation
    public List<RequestHandler.Message> getMessages() {
        return messages;
    }

    // Setter for the messages in the conversation
    public void setMessages(List<RequestHandler.Message> messages) {
        this.messages = messages;
    }
}
