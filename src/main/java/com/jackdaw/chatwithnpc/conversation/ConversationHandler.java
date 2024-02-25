package com.jackdaw.chatwithnpc.conversation;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.api.RequestHandler;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.auxiliary.prompt.Prompt;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ConversationHandler {

    NPCEntity npc;

    PlayerEntity player;

    Prompt prompt;

    long updateTime;

    public ConversationHandler(NPCEntity npc, PlayerEntity player) {
        this.npc = npc;
        this.player = player;
        ChatWithNPCMod.LOGGER.debug("[chat-with-npc] Loading prompt for " + npc.getName());
        this.prompt = npc.getPrompt();
    }

    private void sendWaitMessage() {
        player.sendMessage(Text.of("<" + npc.getName() + "> ..."));
    }

    public void getResponse(PlayerEntity player) {
        // 1.5 second cooldown between requests
        if (npc.getLastMessageTime() + 1500L > System.currentTimeMillis()) return;
        if (SettingManager.apiKey.isEmpty()) {
            player.sendMessage(Text.of("[chat-with-npc] You have not set an API key! Get one from https://beta.openai.com/account/api-keys and set it with /chat-with-npc setkey"));
            return;
        }
        npc.updateLastMessageTime(System.currentTimeMillis());
        Thread t = new Thread(() -> {
            try {
                ChatWithNPCMod.LOGGER.info("[chat-with-npc] Getting response for " + npc.getName());
                String response = RequestHandler.getAIResponse(prompt.getPrompt() + "What is your response: \n");
                player.sendMessage(Text.of("<" + npc.getName() + "> " + response));
                npc.addMessageRecord(npc.getLastMessageTime(), response, npc.getName());
                prompt.addNpcMessage(response);
            } catch (Exception e) {
                player.sendMessage(Text.of("[chat-with-npc] Error getting response"));
                ChatWithNPCMod.LOGGER.error(e.getMessage());
            } finally {
                ChatWithNPCMod.LOGGER.info("[chat-with-npc] Finished getting response");
            }
        });
        t.start();
    }

    public void startConversation() {
        ChatWithNPCMod.LOGGER.debug("[chat-with-npc] Initializing conversation " + npc.getName() + " for " + player.getName());
        sendWaitMessage();
        prompt.setInitialPrompt();
        ChatWithNPCMod.LOGGER.info("[chat-with-npc] The full prompt is: \n" + prompt.getPrompt());
        getResponse(player);
        updateTime = System.currentTimeMillis();
    }

    public void replyToEntity(String message) {
        sendWaitMessage();
        npc.addMessageRecord(System.currentTimeMillis(), message, player.getName().getString());
        prompt.addPlayerMessage(message);
        getResponse(player);
        updateTime = System.currentTimeMillis();
    }

    public long getUpdateTime() {
        return updateTime;
    }

}
