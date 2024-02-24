package com.jackdaw.chatwithnpc.environment;

import com.jackdaw.chatwithnpc.data.EnvironmentDataManager;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class LocalEnvironment implements EnvironmentManager{

    private final String name;

    private String weather = "good";

    private final HashSet<String> environmentPrompt = new HashSet<>();

    private final TreeMap<Long, String> tempEnvironmentPrompt = new TreeMap<>();

    public LocalEnvironment(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPrompt() {
        // 将所有在environmentPrompt和tempEnvironmentPrompt中的提示信息通过;连接起来
        StringBuilder prompt = new StringBuilder();
        for (String s : environmentPrompt) {
            prompt.append(s).append(";");
        }
        for (String s : tempEnvironmentPrompt.values()) {
            prompt.append(s).append(";");
        }
        return "This place is named " + name + ". The weather is " + weather + "And the environment is introduced as:" + prompt;
    }

    @Override
    public String getWeather() {
        return weather;
    }

    @Override
    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public void addTempEnvironmentPrompt(String prompt, long time) {
        tempEnvironmentPrompt.put(time, prompt);
    }

    @Override
    public void popBackTempEnvironmentPrompt() {
        tempEnvironmentPrompt.pollLastEntry();
    }

    @Override
    public void clearTempEnvironmentPrompt(long time) {
        tempEnvironmentPrompt.headMap(time).clear();
    }

    @Override
    public void addEnvironmentPrompt(String prompt) {
        environmentPrompt.add(prompt);
    }

    @Override
    public TreeSet<String> getEnvironmentPrompt() {
        return new TreeSet<>(environmentPrompt);
    }

    @Override
    public TreeMap<Long, String> getTempEnvironmentPrompt() {
        return new TreeMap<>(tempEnvironmentPrompt);
    }

    @Override
    public EnvironmentDataManager getDataManager() {
        return new EnvironmentDataManager(this);
    }
}
