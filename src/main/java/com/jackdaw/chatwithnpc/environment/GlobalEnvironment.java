package com.jackdaw.chatwithnpc.environment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class GlobalEnvironment{
    @Contract(" -> new")
    public static @NotNull LocalEnvironment getGlobalEnvironment(){
        return new LocalEnvironment("Global");
    }
}
