package com.jackdaw.chatwithnpc.environment;

import com.jackdaw.chatwithnpc.data.EnvironmentDataManager;

import java.util.TreeMap;
import java.util.TreeSet;


/**
 * This is an interface used to define the environment.
 * The class should record the basic information of the environment, such as name, weather, environmentPrompt and tempEnvironmentPrompt.
 * The class should provide methods to get the name, prompt, weather, environmentPrompt and tempEnvironmentPrompt of the environment.
 * <p>
 * <b>Please note: This class should be set a life cycle to reduce repeated reading and memory usage<b/>
 *
 * @version 1.0
 */
public interface Environment {
    /**
     * Get the name of the environment.
     * @return the name of the environment.
     */
    String getName();

    /**
     * Get all the prompt of the environment.
     * @return the prompt of the environment.
     */
    String getPrompt();

    /**
     * Get the weather of the environment.
     * @return the weather of the environment.
     */
    String getWeather();

    /**
     * Set the weather of the environment.
     * @param weather the weather of the environment.
     */
    void setWeather(String weather);

    /**
     * Get the last load time of the environment.
     * @return the last load time of the environment.
     */
    long getLastLoadTime();

    /**
     * Set the last load time of the environment.
     * @param time the last load time of the environment.
     */
    void setLastLoadTime(long time);

    /**
     * Add a temporary environment prompt.
     * @param prompt the prompt to be added.
     * @param time the time when the prompt is added.
     */
    void addTempEnvironmentPrompt(String prompt, long time);

    /**
     * Remove the last temporary environment prompt.
     */
    void popBackTempEnvironmentPrompt();

    /**
     * Clear all the temporary environment prompt before the time.
     * @param time the time before which all the temporary environment prompt will be cleared.
     */
    void clearTempEnvironmentPrompt(long time);

    /**
     * Add a environment prompt.
     * @param prompt the prompt to be added.
     */
    void addEnvironmentPrompt(String prompt);

    /**
     * Get all the environment prompt.
     * @return all the environment prompt.
     */
    TreeSet<String> getEnvironmentPrompt();

    /**
     * Get all the temporary environment prompt.
     * @return all the temporary environment prompt.
     */
    TreeMap<Long, String> getTempEnvironmentPrompt();

    /**
     * Get the data manager of the environment.
     * @return the data manager of the environment.
     */
    EnvironmentDataManager getDataManager();
}
