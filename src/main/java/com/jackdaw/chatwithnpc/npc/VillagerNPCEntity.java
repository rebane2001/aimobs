package com.jackdaw.chatwithnpc.npc;

import com.jackdaw.chatwithnpc.auxiliary.prompt.Builder;
import com.jackdaw.chatwithnpc.auxiliary.prompt.Prompt;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

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
        // TODO: 不在这一版本的考虑范围中
    }

    @Override
    public Prompt getPrompt() {
        return new Builder().setFromEntity(this).build();
    }

}
