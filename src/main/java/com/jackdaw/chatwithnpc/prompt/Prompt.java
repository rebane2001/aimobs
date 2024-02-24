package com.jackdaw.chatwithnpc.prompt;

public class Prompt {
    private final String npcName;

    private final String type;

    private final String npcCareer;

    private final String basicPrompt;

    private final String history;

    private final String localEnvironmentPrompt;

    private final String globalEnvironmentPrompt;

    private final String finalPrompt;

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

    public String getPrompt() {
        return finalPrompt;
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

    public String getLocalEnvironmentPrompt() {
        return localEnvironmentPrompt;
    }

    public String getGlobalEnvironmentPrompt() {
        return globalEnvironmentPrompt;
    }
}
