package com.jackdaw.chatwithnpc.npc;

import com.jackdaw.chatwithnpc.data.NPCDataManager;

import java.util.HashMap;


/**
 * This class is used to manage the NPC entities.
 */
public class NPCEntityManager {

    // The time in milliseconds that a conversation is considered out of time
    private static final long outOfTime = 300000L;

    public static final HashMap<String, NPCEntity> npcMap = new HashMap<>();

    public static boolean isConversing(String name) {
        return npcMap.containsKey(name);
    }

    /**
     * Initialize an NPC entity if the NPC is not conversing.
     * @param name The name of the NPC
     * @param npcEntity The NPC entity to initialize
     */
    public static void registerNPCEntity(String name, NPCEntity npcEntity) {
        if (isConversing(name)) {
            return;
        }
        NPCDataManager npcDataManager = new NPCDataManager(npcEntity);
        if (npcDataManager.isExist()) {
            npcDataManager.sync();
        } else {
            npcDataManager.write();
        }
        npcMap.put(npcEntity.getName(), npcEntity);
    }

    public static void removeNPCEntity(String name) {
        npcMap.remove(name);
    }

    public static NPCEntity getNPCEntity(String name) {
        return npcMap.get(name);
    }

    public static void endOutOfTimeConversations() {
        npcMap.forEach((name, npcEntity) -> {
            if (npcEntity.getLastMessageTime() + outOfTime < System.currentTimeMillis()) {
                removeNPCEntity(name);
            }
        });
    }
}
