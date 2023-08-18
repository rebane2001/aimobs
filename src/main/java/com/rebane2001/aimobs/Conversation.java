package com.rebane2001.aimobs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {
    private UUID mobId;
    private List<RequestHandler.Message> messages; // Use RequestHandler.Message

    public Conversation(UUID mobId) {
        this.mobId = mobId;
        this.messages = new ArrayList<>();
    }

    public UUID getMobId() {
        return mobId;
    }

    public void addMessage(String role, String content) {
        messages.add(new RequestHandler.Message(role, content)); // Use RequestHandler.Message
    }

    public List<RequestHandler.Message> getMessages() { // Return type updated
        return messages;
    }

    public void setMessages(List<RequestHandler.Message> messages) { // New method added
        this.messages = messages;
    }
}
