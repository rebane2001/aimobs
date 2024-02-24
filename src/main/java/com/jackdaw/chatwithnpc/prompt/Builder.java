package com.jackdaw.chatwithnpc.prompt;

import com.jackdaw.chatwithnpc.environment.EnvironmentManager;
import com.jackdaw.chatwithnpc.environment.GlobalEnvironment;
import com.jackdaw.chatwithnpc.environment.LocalEnvironment;
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
        EnvironmentManager localEnvironment = new LocalEnvironment(npc.getLocalGroup());
        this.localEnvironmentPrompt = localEnvironment.getPrompt();
        return this;
    }

    public Builder setLocalEnvironment(@NotNull EnvironmentManager localEnvironment) {
        this.localEnvironmentPrompt = localEnvironment.getPrompt();
        return this;
    }

    public Builder setFromPrompt(@NotNull Prompt prompt) {
        this.npcName = prompt.getNpcName();
        this.type = prompt.getType();
        this.npcCareer = prompt.getNpcCareer();
        this.basicPrompt = prompt.getBasicPrompt();
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

    public Prompt build() {
        String finalPrompt =
                "This is " + npcName + " who is a " + npcCareer + " and is a " + type
                        + ".\n He possesses the following characteristics:\n" + basicPrompt
                        + ".\n He is living in this small places (villages or cities): \n" + localEnvironmentPrompt
                        + ".\n And he lives on this continent: \n" + globalEnvironmentPrompt
                        + ".\n And he has the following chat log: \n" + history;
        return new Prompt(npcName, type, npcCareer, basicPrompt, history, localEnvironmentPrompt, globalEnvironmentPrompt, finalPrompt);
    }

}
