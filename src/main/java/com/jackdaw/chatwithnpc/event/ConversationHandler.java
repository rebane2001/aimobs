package com.jackdaw.chatwithnpc.event;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.api.RequestHandler;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.mixin.ChatHudAccessor;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import com.jackdaw.chatwithnpc.auxiliary.prompt.Prompt;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class ConversationHandler {

    NPCEntity npc;

    PlayerEntity player;

    Prompt prompt;

    long updateTime;

    // The waitMessage is the thing that goes '<Name> ...' before an actual response is received
    private static ChatHudLine.Visible waitMessage;
    public ConversationHandler(NPCEntity npc, PlayerEntity player) {
        this.npc = npc;
        this.player = player;
        this.prompt = npc.getPrompt();
    }

    private static List<ChatHudLine.Visible> getChatHudMessages() {
        return ((ChatHudAccessor) MinecraftClient.getInstance().inGameHud.getChatHud()).getVisibleMessages();
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

    public void getResponse(PlayerEntity player) {
        // 1.5 second cooldown between requests
        if (npc.getLastMessageTime() + 1500L > System.currentTimeMillis()) return;
        if (SettingManager.apiKey.isEmpty()) {
            player.sendMessage(Text.of("You have not set an API key! Get one from https://beta.openai.com/account/api-keys and set it with /chat-with-npc setkey"));
            return;
        }
        npc.updateLastMessageTime(System.currentTimeMillis());
        Thread t = new Thread(() -> {
            try {
                String response = RequestHandler.getAIResponse(npc.getPrompt().getPrompt());
                player.sendMessage(Text.of("<" + npc.getName() + "> " + response));
                npc.addMessageRecord(npc.getLastMessageTime(), response, npc.getName());
                prompt.addNpcMessage(response);
            } catch (Exception e) {
                player.sendMessage(Text.of("Error getting response"));
                ChatWithNPCMod.LOGGER.error(e.getMessage());
            } finally {
                hideWaitMessage();
            }
        });
        t.start();
    }

    public void startConversation() {
        showWaitMessage(npc.getName());
        prompt.setInitialPrompt();
        getResponse(player);
        updateTime = System.currentTimeMillis();
    }

    public void replyToEntity(String message) {
        npc.addMessageRecord(System.currentTimeMillis(), message, player.getName().getString());
        prompt.addPlayerMessage(message);
        getResponse(player);
        updateTime = System.currentTimeMillis();
    }

    public long getUpdateTime() {
        return updateTime;
    }

}
