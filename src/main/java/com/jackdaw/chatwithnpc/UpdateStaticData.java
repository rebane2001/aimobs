package com.jackdaw.chatwithnpc;

import com.jackdaw.chatwithnpc.environment.EnvironmentManager;
import com.jackdaw.chatwithnpc.event.ConversationManager;
import com.jackdaw.chatwithnpc.npc.NPCEntityManager;

public class UpdateStaticData {
    public static void update() {
        ConversationManager.endOutOfTimeConversations();
        NPCEntityManager.endOutOfTimeNPCEntity();
        EnvironmentManager.endOutOfTimeEnvironments();
    }
}
