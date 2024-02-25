package com.jackdaw.chatwithnpc.npc;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.data.NPCDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;

import java.util.HashMap;


/**
 * This class is used to manage the NPC entities.
 */
public class NPCEntityManager {

    // The time in milliseconds that an NPCEntity is considered out of time
    private static final long outOfTime = ChatWithNPCMod.outOfTime;

    public static final HashMap<String, NPCEntity> npcMap = new HashMap<>();

    public static boolean isRegistered(String name) {
        return npcMap.containsKey(name);
    }

    /**
     * Initialize an NPC entity if the NPC is not conversing.
     * @param name The name of the NPC
     * @param entity The NPC entity to initialize
     */
    public static void registerNPCEntity(String name, Entity entity) {
        ChatWithNPCMod.LOGGER.debug("[chat-with-npc] Registering NPC: " + name);
        if (isRegistered(name)) {
            return;
        }
        NPCEntity npcEntity;
        if (entity instanceof VillagerEntity villager) {
            npcEntity = new VillagerNPCEntity(villager);
        } else if (entity instanceof LivingEntity entityLiving) {
            npcEntity = new LivingNPCEntity(entityLiving);
        } else {
            return;
        }
        ChatWithNPCMod.LOGGER.debug("[chat-with-npc] loading NPC: " + name);
        NPCDataManager npcDataManager = npcEntity.getDataManager();
        if (npcDataManager.isExist()) {
            ChatWithNPCMod.LOGGER.debug("[chat-with-npc] The NPC has data file. Syncing the data.");
            npcDataManager.sync();
        } else {
            ChatWithNPCMod.LOGGER.debug("[chat-with-npc] The NPC doesn't have data file. Saving the data with default setting.");
            npcDataManager.save();
        }
        ChatWithNPCMod.LOGGER.debug("[chat-with-npc] The NPC has been loaded and saved.");
        npcMap.put(name, npcEntity);
    }

    public static void removeNPCEntity(String name) {
        npcMap.remove(name);
    }

    public static NPCEntity getNPCEntity(String name) {
        return npcMap.get(name);
    }

    public static void endOutOfTimeNPCEntity() {
        npcMap.forEach((name, npcEntity) -> {
            if (npcEntity.getLastMessageTime() + outOfTime < System.currentTimeMillis()) {
                npcEntity.getDataManager().save();
                removeNPCEntity(name);
            }
        });
    }
}
