package com.jackdaw.chatwithnpc.npc;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class VillagerNPCEntity extends NPCEntity {

    /**
     * This is a constructor used to initialize the NPC with the entity.
     *
     * @param entity The entity of the NPC.
     */
    public VillagerNPCEntity(@NotNull VillagerEntity entity) {
        super(entity);
    }

    @Override
    public void replyMessage(String message, PlayerEntity player) {
        // TODO: Implement this method
    }

    @Override
    public void doAction(Actions action, PlayerEntity player) {
        // TODO: Implement this method
    }

    @Override
    public String getPrompt() {
        // TODO: Implement this method
        return null;
    }

    @Override
    public TreeMap<Long, String> getChatHistory() {
        // TODO: Implement this method
        return null;
    }
}
