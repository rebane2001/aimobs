package com.jackdaw.chatwithnpc.auxiliary.prompt;

public class Prompt {
    private final String npcName;

    private final String type;

    private final String npcCareer;

    private final String basicPrompt;

    private final String history;

    private final String localEnvironmentPrompt;

    private final String globalEnvironmentPrompt;

    private String finalPrompt;

    Prompt(String npcName, String type, String npcCareer, String basicPrompt, String history, String localEnvironmentPrompt, String globalEnvironmentPrompt, String finalPrompt) {
        this.npcName = npcName;
        this.type = type;
        this.npcCareer = npcCareer;
        this.basicPrompt = basicPrompt;
        this.history = history;
        this.localEnvironmentPrompt = localEnvironmentPrompt;
        this.globalEnvironmentPrompt = globalEnvironmentPrompt;
        this.finalPrompt = finalPrompt;
    }

    /**
     * 获取完整的NPC的提示信息。
     * @return NPC的提示信息。
     */
    public String getPrompt() {
        return finalPrompt;
    }

    /**
     * 添加一个提示。
     * @param prompt 要添加的提示。
     */
    public void addPrompt(String prompt) {
        finalPrompt += prompt;
    }

    /**
     * 设置会话初始提示。
     */
    public void setInitialPrompt() {
        addPrompt("You are playing as NPC named "+ npcName + " talking to the Player. You start the conversation:");
    }

    /**
     * 添加玩家的新消息。
     * @param message 玩家的消息。
     */
    public void addPlayerMessage(String message) {
        addPrompt("The player say: " + message + "\n");
    }

    /**
     * 添加NPC的新消息。
     * @param message NPC的消息。
     */
    public void addNpcMessage(String message) {
        addPrompt("The " + npcName + " says: " + message + "\n");
    }

    public String getNpcName() {
        return npcName;
    }

    public String getType() {
        return type;
    }

    public String getNpcCareer() {
        return npcCareer;
    }

    public String getBasicPrompt() {
        return basicPrompt;
    }

    public String getHistory() {
        return history;
    }

    public String getLocalEnvironmentPrompt() {
        return localEnvironmentPrompt;
    }

    public String getGlobalEnvironmentPrompt() {
        return globalEnvironmentPrompt;
    }
}
