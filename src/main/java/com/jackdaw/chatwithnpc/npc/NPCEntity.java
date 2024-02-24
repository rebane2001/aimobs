package com.jackdaw.chatwithnpc.npc;

import com.jackdaw.chatwithnpc.data.NPCDataManager;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

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
public abstract class NPCEntity implements NPCHandler {

    protected Entity entity;
    protected final String name;

    protected final String type;

    protected final UUID uuid;
    protected String career = "";
    protected String basicPrompt = "";
    protected String localGroup = "";

    protected long lastMessageTime = 0L;

    protected final TreeMap<Long, String> messageRecord = new TreeMap<>();

    /**
     * This is a constructor used to initialize the NPC with the entity.
     * @param entity The entity of the NPC.
     */
    public NPCEntity(@NotNull Entity entity) {
        if (entity.getCustomName() == null) {
            throw new IllegalArgumentException("The entity must have a custom name.");
        }
        this.name = entity.getCustomName().getString();
        this.type = entity.getName().getString();
        this.uuid = entity.getUuid();
        this.entity = entity;
    }

    /**
     * 获取NPC的名字，该名字应该作为该NPC在本插件中的唯一标识，并将作为储存的文件名。
     * @return NPC的名字
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取NPC的类型，该类型应该作为该NPC的特征之一。
     * @return NPC的类型
     */
    public String getType() {
        return this.type;
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
    public void updateLastMessageTime(long lastMessageTime) {
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
     * 读取所有消息记录。
     * @return 消息记录
     */
    public String readMessageRecord() {
        // 一行一行地读取消息记录
        StringBuilder messageRecord = new StringBuilder();
        for (String message : this.messageRecord.values()) {
            messageRecord.append(message).append("\n");
        }
        return messageRecord.toString();
    }

    /**
     * 获取NPC的数据管理器，该管理器应该用于管理NPC的数据。
     * @return NPC的数据管理器
     */
    public NPCDataManager getDataManager() {
        return new NPCDataManager(this);
    }
}
