package com.jackdaw.chatwithnpc.npc;

import com.jackdaw.chatwithnpc.data.DataManager;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.TreeMap;
import java.util.UUID;

/**
 * 这是一个用于定义与 NPC 交互的类。
 * 该类应记录 NPC 的基本信息，如名称、UUID、职业、basicPrompt 和 localGroup 等，这些都是 NPC 的特征。
 * 同时该类应记录 NPC 的对话状态，如最后一次消息时间、消息记录和是否正在与玩家对话等。
 * 该类应提供接收玩家信息、回复玩家信息和执行动作等方法。
 * <p>
 * <b>请注意：该类应该设置一个生命周期以降低重复读取和内存占用<b/>
 *
 * @version 1.0
 */
public abstract class NPCEntity {
    protected final String name;

    protected final UUID uuid;
    protected String career = "";
    protected String basicPrompt = "";
    protected String localGroup = "";

    protected long lastMessageTime = 0L;

    protected boolean isTalking = false;

    protected final TreeMap<Long, String> messageRecord = new TreeMap<>();

    /**
     * This is a constructor used to initialize the NPC with the entity.
     * @param entity The entity of the NPC.
     */
    public NPCEntity(@NotNull Entity entity) {
        this.name = entity.getName().getString();
        this.uuid = entity.getUuid();
    }

    /**
     * 操作该NPC回复玩家信息。
     * @param message 该NPC应该回复玩家的消息。
     */
    public abstract void replyMessage(String message);

    /**
     * 操作该NPC执行动作。
     * @param action 该NPC应该回应玩家的动作。
     */
    public abstract void doAction(Actions action);

    /**
     * 获取NPC的名字，该名字应该作为该NPC在本插件中的唯一标识，并将作为储存的文件名。
     * @return NPC的名字
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取NPC的UUID，该UUID应该作为该NPC在Minecraft中的唯一标识。
     * @return NPC的UUID
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * 获取NPC的职业，该职业应该作为该NPC的特征之一。
     * @return NPC的职业
     */
    public String getCareer() {
        return this.career;
    }

    /**
     * 获取NPC的基本提示信息，该信息应该作为该NPC的基本信息之一。
     * @return NPC的基本提示信息
     */
    public String getBasicPrompt() {
        return this.basicPrompt;
    }

    /**
     * 获取NPC的本地群组，该群组应该作为该NPC的特征之一，即该NPC的所在位置相关信息。
     * @return NPC的本地群组
     */
    public String getLocalGroup() {
        return this.localGroup;
    }

    /**
     * 获取NPC的最后一次消息时间，随着时间的退役该NPC会逐渐遗忘他讲过的内容。
     * @return NPC的最后一次消息时间
     */
    public long getLastMessageTime() {
        return this.lastMessageTime;
    }

    /**
     * 获取NPC的消息记录，该记录应该包括了NPC的所有消息。
     * @return NPC的消息记录
     */
    public TreeMap<Long, String> getMessageRecord() {
        return this.messageRecord;
    }

    /**
     * 获取NPC的对话状态，该状态应该用于判断NPC是否正在与玩家对话。
     * @return NPC的对话状态
     */
    public boolean getIsTalking() {
        return this.isTalking;
    }

    /**
     * 设置NPC的职业，该职业应该作为该NPC的特征之一。
     * @param career NPC的职业
     */
    public void setCareer(String career) {
        this.career = career;
    }

    /**
     * 设置NPC的基本提示信息，该信息应该作为该NPC的基本信息之一。
     * @param basicPrompt NPC的基本提示信息
     */
    public void setBasicPrompt(String basicPrompt) {
        this.basicPrompt = basicPrompt;
    }

    /**
     * 设置NPC的本地群组，该群组应该作为该NPC的特征之一，即该NPC的所在位置相关信息。
     * @param localGroup NPC的本地群组
     */
    public void setLocalGroup(String localGroup) {
        this.localGroup = localGroup;
    }

    /**
     * 设置NPC的最后一次消息时间，随着时间的退役该NPC会逐渐遗忘他讲过的内容。
     * @param lastMessageTime NPC的最后一次消息时间
     */
    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    /**
     * 添加NPC的消息记录，该记录应该包括了NPC的最近一条消息。
     * @param time NPC的消息时间
     * @param message NPC的消息内容
     */
    public void addMessageRecord(long time, String message) {
        this.messageRecord.put(time, message);
    }

    /**
     * 删除NPC的消息记录，删除该时间以前的所有记录。
     * @param time 保存的截止时间
     */
    public void deleteMessageRecord(long time) {
        this.messageRecord.headMap(time).clear();
    }

    /**
     * 设置NPC的对话状态，该状态应该用于判断NPC是否正在与玩家对话。
     * @param isTalking NPC的对话状态
     */
    public void setIsTalking(boolean isTalking) {
        this.isTalking = isTalking;
    }

    /**
     * 获取NPC的数据管理器，该管理器应该用于管理NPC的数据。
     * @param workingDirectory 工作目录
     * @param logger 日志记录器
     * @return NPC的数据管理器
     */
    public DataManager getDataManager(File workingDirectory, Logger logger) {
        return new DataManager(workingDirectory, logger, this);
    }
}
