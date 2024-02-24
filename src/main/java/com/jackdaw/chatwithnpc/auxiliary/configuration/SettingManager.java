package com.jackdaw.chatwithnpc.auxiliary.configuration;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.auxiliary.yaml.YamlUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Manages the config files for the modules provided by this plugin.
 *
 * <p>
 * Configure the setting if a specific module is enabled or disabled.
 *
 * @author WDRshadow
 * @version v1.0
 */
public class SettingManager {
    private static final Logger logger = ChatWithNPCMod.LOGGER;
    private static final File configFile = ChatWithNPCMod.workingDirectory.resolve("config.yml").toFile();

    // use for confirming the setting version is the same with the plugin
    private static final String lastVersion = "v1.0";

    public static boolean enabled = true;

    public static String language = "zh";
    public static String apiKey = "";
    public static String model = "text-davinci-003";
    public static float temperature = 0.6f;


    /**
     * Load the setting from the config file.
     */
    public static void loadConfig(){
        if (configFile.exists()) {
            try {
                HashMap data = YamlUtils.readFile(configFile);
                String version = (String) data.get("version");
                if (!lastVersion.equals(version)) {
                    logger.warn("The config file is not the same version with the plugin.");
                    write();
                }
                SettingManager.enabled = (boolean) data.get("enabled");
                SettingManager.language = (String) data.get("language");
                SettingManager.apiKey = (String) data.get("apiKey");
                SettingManager.model = (String) data.get("model");
                SettingManager.temperature = (float) data.get("temperature");
            } catch (FileNotFoundException e) {
                logger.error("Can't open the config file.");
            }
        } else {
            write();
        }
    }

    /**
     * Write the setting to the config file.
     */
    public static void write() {
        try {
            if (!configFile.exists()) {
                if (!configFile.createNewFile()) {
                    logger.error("Can't create the config file.");
                    return;
                }
            }
            HashMap data = new HashMap();
            data.put("version", lastVersion);
            data.put("enabled", SettingManager.enabled);
            data.put("language", SettingManager.language);
            data.put("apiKey", SettingManager.apiKey);
            data.put("model", SettingManager.model);
            data.put("temperature", SettingManager.temperature);
            YamlUtils.writeFile(configFile, data);
        } catch (IOException e) {
            logger.error("Can't write the config file.");
        }
    }
}
