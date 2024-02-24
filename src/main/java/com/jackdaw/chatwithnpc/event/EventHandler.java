package com.jackdaw.chatwithnpc.event;

import com.jackdaw.chatwithnpc.mixin.ChatHudAccessor;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.List;

public class EventHandler {

    NPCEntity npc;

    PlayerEntity player;

    // The waitMessage is the thing that goes '<Name> ...' before an actual response is received
    private static ChatHudLine.Visible waitMessage;
    public EventHandler(NPCEntity npc, PlayerEntity player) {
        this.npc = npc;
        this.player = player;
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

    public void startConversation() {
        showWaitMessage(npc.getName());
        // TODO: Add the actual conversation code here
    }

}
