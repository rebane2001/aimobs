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
        NPCDataManager npcDataManager = npcEntity.getDataManager();
        if (npcDataManager.isExist()) {
            npcDataManager.sync();
        } else {
            npcDataManager.save();
        }
        npcMap.put(name, npcEntity);
    }

    public static void removeNPCEntity(String name) {
        npcMap.get(name).getDataManager().save();
        npcMap.remove(name);
    }

    public static NPCEntity getNPCEntity(String name) {
        return npcMap.get(name);
    }

    public static void endOutOfTimeNPCEntity() {
        npcMap.forEach((name, npcEntity) -> {
            if (npcEntity.getLastMessageTime() + outOfTime < System.currentTimeMillis()) {
                removeNPCEntity(name);
            }
        });
    }

    public static void endAllNPCEntity() {
        npcMap.forEach((name, npcEntity) -> {
            removeNPCEntity(name);
        });
    }
}
