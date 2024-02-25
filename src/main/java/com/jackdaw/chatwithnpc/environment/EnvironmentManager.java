package com.jackdaw.chatwithnpc.environment;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.data.DataManager;

import java.util.HashMap;

/**
 * This class is used to manage the environment of the game.
 */
public class EnvironmentManager {
    // The time in milliseconds that an environment is considered out of time
    private static final long outOfTime = ChatWithNPCMod.outOfTime;
    public static final HashMap<String, Environment> environmentMap = new HashMap<>();

    public static boolean isLoaded(String name) {
        return environmentMap.containsKey(name);
    }


    /**
     * Initialize an environment if the environment is not conversing.
     * @param name The name of the environment
     */
    public static void loadEnvironment(String name) {
        Environment environment;
        if (name.equals("Global")) {
            environment = new GlobalEnvironment("Global");
        } else {
            environment = new LocalEnvironment(name);
        }
        if (isLoaded(name)) {
            return;
        }
        DataManager environmentDataManager = environment.getDataManager();
        if (environmentDataManager.isExist()) {
            environmentDataManager.sync();
        } else {
            environmentDataManager.save();
        }
        environmentMap.put(name, environment);
    }

    public static void removeEnvironment(String name) {
        environmentMap.get(name).getDataManager().save();
        environmentMap.remove(name);
    }

    public static Environment getEnvironment(String name) {
        return environmentMap.get(name);
    }

    public static void endOutOfTimeEnvironments() {
        environmentMap.forEach((name, environment) -> {
            if (environment.getName().equals("Global")) {
                return;
            }
            if (environment.getLastLoadTime() + outOfTime < System.currentTimeMillis()) {
                environment.getDataManager().save();
                removeEnvironment(name);
            }
        });
    }

    public static void endAllEnvironments() {
        environmentMap.forEach((name, environment) -> {
            removeEnvironment(name);
        });
    }
}
