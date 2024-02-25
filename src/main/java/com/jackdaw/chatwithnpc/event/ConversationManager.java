package com.jackdaw.chatwithnpc.event;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;

/**
 * This class is used to manage conversations between players and NPCs.
 * <p>
 * <b>Every player can only have one conversation at a time.</b>
 */
public class ConversationManager {

    // The time in milliseconds that a conversation is considered out of time
    private static final long outOfTime = ChatWithNPCMod.outOfTime;
    public static final HashMap<PlayerEntity, ConversationHandler> conversationMap = new HashMap<>();

    /**
     * Start a conversation for Player with an NPC
     * @param npc The NPC to start a conversation with
     * @param player The player to start a conversation with
     */
    public static void startConversation(NPCEntity npc, PlayerEntity player) {
        if (npc == null || player == null) return;
        findAndEndConversation(player);
        ConversationHandler conversationHandler = new ConversationHandler(npc, player);
        conversationHandler.startConversation();
        conversationMap.put(player, conversationHandler);
    }

    private static void endConversation(PlayerEntity player) {
        conversationMap.remove(player);
    }

    // must check if the player is conversing before calling this method
    public static ConversationHandler getConversation(PlayerEntity player) {
        return conversationMap.get(player);
    }

    public static boolean isConversing(PlayerEntity player) {
        return conversationMap.containsKey(player);
    }

    private static void findAndEndConversation(PlayerEntity player) {
        if (isConversing(player)) {
            endConversation(player);
        }
    }

    public static void endOutOfTimeConversations() {
        conversationMap.forEach((player, conversationHandler) -> {
            if (conversationHandler.getUpdateTime() + outOfTime < System.currentTimeMillis()) {
                endConversation(player);
            }
        });
    }

}
