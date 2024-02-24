package com.jackdaw.chatwithnpc.npc;

// 这是一个接口，用于定义与NPC交互的方法；其中的交互应该包括接收回复玩家信息，执行动作，获取NPC状态
public interface NpcInteractionHandler {
    /**
     * 回复玩家信息
     * @param message 该NPC应该回复玩家的消息。
     */
    void replyMessage(String message);

    /**
     * 执行动作
     * @param action 该NPC应该回应玩家的动作。
     */
    void doAction(Actions action);
}
