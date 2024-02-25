package com.jackdaw.chatwithnpc.data;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.auxiliary.yaml.YamlUtils;
import com.jackdaw.chatwithnpc.environment.Environment;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * A serializer used to read or write the data from the files.
 * <p>
 * This data is related to the Environment's content.
 *
 * <p>Read or Write the data file with some information, each file just record one relative information.</p>
 */
public class EnvironmentDataManager implements DataManager {

    private static final Logger logger = ChatWithNPCMod.LOGGER;
    private final File theFile;

    private final Environment environment;

    public EnvironmentDataManager(Environment environment) {
        this.environment = environment;
        this.theFile = new File(ChatWithNPCMod.workingDirectory.toFile(), "environment/" + environment.getName() + ".yml");
    }

    @Override
    public boolean isExist() {
        return theFile.exists();
    }

    @Override
    public void sync() {
        if (!isExist()) {
            return;
        }
        try {
            HashMap data = YamlUtils.readFile(theFile);
            // 读取储存在文件中的数据，然后将其赋值给environment
            environment.setWeather((String) data.get("weather"));
            // 在data中读取存在environmentPrompt中的数据
            for (Object s : (Iterable) data.get("environmentPrompt")) {
                environment.addEnvironmentPrompt((String) s);
            }
            // 在data中读取存在tempEnvironmentPrompt中的数据
            HashMap tempEnvironmentPrompt = (HashMap) data.get("tempEnvironmentPrompt");
            for (Object key : tempEnvironmentPrompt.keySet()) {
                environment.addTempEnvironmentPrompt((String) tempEnvironmentPrompt.get(key), (long) key);
            }
        } catch (FileNotFoundException e) {
            logger.error("[chat-with-npc] Can't open the data file.");
        }
    }

    @Override
    public void save() {
        try {
            if (!isExist()) {
                if (!theFile.createNewFile()) {
                    logger.error("[chat-with-npc] Can't create the data file.");
                    return;
                }
            }
            HashMap<String, Object> data = new HashMap<>();
            data.put("weather", environment.getWeather());
            HashSet<String> environmentPrompt = new HashSet<>(environment.getEnvironmentPrompt());
            data.put("environmentPrompt", environmentPrompt);
            HashMap<Long, String> tempEnvironmentPrompt = new HashMap<>();
            for (long time : environment.getTempEnvironmentPrompt().keySet()) {
                tempEnvironmentPrompt.put(time, environment.getTempEnvironmentPrompt().get(time));
            }
            data.put("tempEnvironmentPrompt", tempEnvironmentPrompt);
            YamlUtils.writeFile(theFile, data);
        } catch (IOException e) {
            logger.error("[chat-with-npc] Can't write the data file.");
        }
    }

    @Override
    public void delete() {
        if (!isExist()) {
            logger.warn("[chat-with-npc] The data file doesn't exist.");
            return;
        }
        if (!theFile.delete()) {
            logger.error("[chat-with-npc] Can't delete the data file.");
        }
    }
}
