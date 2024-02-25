package com.jackdaw.chatwithnpc.environment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GlobalEnvironment extends LocalEnvironment implements Environment{
    GlobalEnvironment(String name) {
        super(name);
    }

    /**
     * 获取全局环境的实例
     * @return 全局环境的实例
     */
    @Contract(" -> new")
    public static @NotNull GlobalEnvironment getGlobalEnvironment(){
        GlobalEnvironment globalEnvironment = (GlobalEnvironment) EnvironmentManager.getEnvironment("Global");
        if (globalEnvironment == null){
            globalEnvironment = new GlobalEnvironment("Global");
            EnvironmentManager.loadEnvironment("Global");
        }
        return globalEnvironment;
    }
}
