package com.jackdaw.chatwithnpc.auxiliary.prompt;

import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.environment.Environment;
import com.jackdaw.chatwithnpc.environment.EnvironmentManager;
import com.jackdaw.chatwithnpc.environment.GlobalEnvironment;
import com.jackdaw.chatwithnpc.npc.NPCEntity;
import org.jetbrains.annotations.NotNull;

public class Builder {
    private String npcName;

    private String type;

    private String npcCareer;

    private String basicPrompt;

    private String localEnvironmentPrompt;

    private static final String globalEnvironmentPrompt = GlobalEnvironment.getGlobalEnvironment().getPrompt();

    private String history;

    public Builder setFromEntity(@NotNull NPCEntity npc) {
        this.npcName = npc.getName();
        this.type = npc.getType();
        this.npcCareer = npc.getCareer();
        this.basicPrompt = npc.getBasicPrompt();
        this.history = npc.readMessageRecord();
        if (!EnvironmentManager.isLoaded(npc.getLocalGroup())) {
            EnvironmentManager.loadEnvironment(npc.getLocalGroup());
        }
        Environment localEnvironment = EnvironmentManager.getEnvironment(npc.getLocalGroup());
        this.localEnvironmentPrompt = localEnvironment.getPrompt();
        return this;
    }

    public Builder setLocalEnvironment(@NotNull Environment localEnvironment) {
        this.localEnvironmentPrompt = localEnvironment.getPrompt();
        return this;
    }

    public Builder setFromPrompt(@NotNull Prompt prompt) {
        this.npcName = prompt.getNpcName();
        this.type = prompt.getType();
        this.npcCareer = prompt.getNpcCareer();
        this.basicPrompt = prompt.getBasicPrompt();
        this.history = prompt.getHistory();
        this.localEnvironmentPrompt = prompt.getLocalEnvironmentPrompt();
        return this;
    }

    public Builder setNpcName(String npcName) {
        this.npcName = npcName;
        return this;
    }

    public Builder setType(String type) {
        this.type = type;
        return this;
    }

    public Builder setNpcCareer(String npcCareer) {
        this.npcCareer = npcCareer;
        return this;
    }

    public Builder setBasicPrompt(String basicPrompt) {
        this.basicPrompt = basicPrompt;
        return this;
    }

    public Builder setLocalEnvironmentPrompt(String localEnvironmentPrompt) {
        this.localEnvironmentPrompt = localEnvironmentPrompt;
        return this;
    }

    /**
     * 构建一个Prompt对象。这将包含NPC的所有基础信息。包括NPC的名字，类型，职业，基础提示，历史聊天记录，本地环境提示和全局环境提示。但不包括当前会话的聊天记录。
     * @return 一个Prompt对象。
     */
    public Prompt build() {
        String languagePrompt = "This conversation is using" + SettingManager.language + " as the language. Please use the same language to continue the conversation.";
        String finalPrompt =
                languagePrompt
                        + "\n" + "This is " + npcName + " who is a " + npcCareer + " and is a " + type
                        + ".\n He possesses the following characteristics:\n" + basicPrompt
                        + ".\n He is living in this small places (villages or cities): \n" + localEnvironmentPrompt
                        + globalEnvironmentPrompt
                        + ".\n And he has the following chat log: \n" + history
                        + ".\n Now please continue the conversation: \n";
        return new Prompt(npcName, type, npcCareer, basicPrompt, history, localEnvironmentPrompt, globalEnvironmentPrompt, finalPrompt);
    }

}
