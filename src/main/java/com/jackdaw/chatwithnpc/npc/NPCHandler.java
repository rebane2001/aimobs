package com.jackdaw.chatwithnpc.npc;

import com.jackdaw.chatwithnpc.auxiliary.prompt.Prompt;
import com.jackdaw.chatwithnpc.data.NPCDataManager;
import net.minecraft.entity.player.PlayerEntity;

import java.util.TreeMap;

public interface NPCHandler {

    /**
     * 操作该NPC回复玩家信息。
     * @param message 该NPC应该回复玩家的消息。
     * @param player 该NPC应该回复的玩家。
     */
    void replyMessage(String message, PlayerEntity player);

    /**
     * 操作该NPC执行动作。
     * @param action 该NPC应该回应玩家的动作。
     * @param player 该NPC应该回应的玩家。
     */
    void doAction(Actions action, PlayerEntity player);

    /**
     * 创建一个提示信息，用于输入至ChatGPT中获取信息。该prompt包括该NPC的各类特征，包括本地环境和全剧环境以及玩家的信息。
     * @return 该NPC的提示信息。
     */
    Prompt getPrompt();

    /**
     * 获取该NPC的历史聊天记录。
     * @return 该NPC的聊天记录。
     */
    String readMessageRecord();

    /**
     * 获取该NPC的数据管理器。
     * @return 该NPC的数据管理器。
     */
    NPCDataManager getDataManager();
}
