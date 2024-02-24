package com.jackdaw.chatwithnpc.prompt;

import com.jackdaw.chatwithnpc.environment.EnvironmentManager;
import com.jackdaw.chatwithnpc.environment.GlobalEnvironment;
import com.jackdaw.chatwithnpc.environment.LocalEnvironment;
import com.jackdaw.chatwithnpc.npc.NPCEntity;

public class Builder {
    private String npcName;

    private String type;

    private String npcCareer;

    private String basicPrompt;

    private String localEnvironmentPrompt;

    private static final String globalEnvironmentPrompt = GlobalEnvironment.getGlobalEnvironment().getPrompt();

    public Builder setFromEntity(NPCEntity npc) {
        this.npcName = npc.getName();
        this.type = npc.getType();
        this.npcCareer = npc.getCareer();
        this.basicPrompt = npc.getBasicPrompt();
        EnvironmentManager localEnvironment = new LocalEnvironment(npc.getLocalGroup());
        this.localEnvironmentPrompt = localEnvironment.getPrompt();
        return this;
    }

    public Builder setLocalEnvironment(EnvironmentManager localEnvironment) {
        this.localEnvironmentPrompt = localEnvironment.getPrompt();
        return this;
    }

    public Builder setFromPrompt(Prompt prompt) {
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
                        + ". He possesses the following characteristics:" + basicPrompt
                        + ". He is living in this small places (villages or cities): " + localEnvironmentPrompt
                        + ". And he lives on this continent: " + globalEnvironmentPrompt;
        return new Prompt(npcName, type, npcCareer, basicPrompt, localEnvironmentPrompt, globalEnvironmentPrompt, finalPrompt);
    }

}
