package com.jackdaw.chatwithnpc.auxiliary.configuration;

import com.moandjiezana.toml.Toml;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Manages the config files for the modules provided by this plugin.
 *
 * <p>
 * Configure the setting if a specific module is enable or disable.
 *
 * @author WDRshadow
 * @version v1.1
 */
public class SettingManager {
    private final Logger logger;
    private final File workingDirectory;
    private final File configFile;

    // use for confirming the setting version is the same with the plugin
    private static final String lastVersion = "v1.1";

    private boolean fastGameModeSettingEnabled;
    private boolean teleportEnabled;
    private boolean giveOPEnabled;

    private boolean tempEnabled;

    /**
     * Instantiates a new Setting manager.
     *
     * @param workingDirectory the directory to store the config file
     * @throws IOException the io exception. The client who
     *                     create the instance is responsible for this.
     */
    public SettingManager(File workingDirectory, Logger logger) throws IOException {
        this.logger = logger;
        this.workingDirectory = workingDirectory;
        this.configFile = new File(this.workingDirectory, "config.toml");
        setUp();
    }

    private void setUp() throws IOException {
        saveDefaultConfig();
        Toml toml = new Toml().read(new File(workingDirectory, "config.toml"));
        this.fastGameModeSettingEnabled = toml.getBoolean("fastGameModeSetting.enabled");
        this.teleportEnabled = toml.getBoolean("teleport.enabled");
        this.giveOPEnabled = toml.getBoolean("giveOP.enabled");
        this.tempEnabled = toml.getBoolean("giveOP.temp");
    }

    private void saveDefaultConfig() throws IOException {
        if (!workingDirectory.exists()) {
            boolean aBoolean = workingDirectory.mkdir();
            if (!aBoolean) logger.warn("Could Not make a new config.toml file.");
        }
        if (configFile.exists()) {
            Toml toml = new Toml().read(new File(workingDirectory, "config.toml"));
            String v;
            try {
                v = toml.getString("version.version");
                if (v.equals(lastVersion)) {
                    return;
                }
            } catch (Exception ignored) {
            }
            boolean aBoolean = configFile.delete();
            if (!aBoolean) logger.warn("Could Not delete old config file.");
        }
        newConfig();
    }

    private void newConfig() throws IOException {
        InputStream in = SettingManager.class.getResourceAsStream("/config.toml");
        try (in) {
            assert in != null;
            Files.copy(in, configFile.toPath());
        }
    }

    /**
     * Is tab fastGameModeSetting enabled.
     *
     * @return the boolean, i.e. enabled returns true; otherwise, false.
     */
    public boolean isFastGameModeSettingEnabled() {
        return fastGameModeSettingEnabled;
    }

    /**
     * Is teleport enabled.
     *
     * @return the boolean, i.e. enabled returns true; otherwise, false.
     */
    public boolean isTeleportEnabled() {
        return teleportEnabled;
    }

    /**
     * Is giveOP enabled.
     *
     * @return the boolean, i.e. enabled returns true; otherwise, false.
     */
    public boolean isGiveOPEnabled() {
        return giveOPEnabled;
    }

    /**
     * Is giveOP temporary enabled.
     *
     * @return the boolean, i.e. enabled returns true; otherwise, false.
     */
    public boolean isTempEnabled() {
        return tempEnabled;
    }

}
