package com.rebane2001.aimobs;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Objects;

public class AIMobsConfig {
    public static class Config {
        public boolean enabled = true;
        public String apiKey = "";
        public String model = "text-davinci-003";
        public float temperature = 0.6f;
    }
    public static Config config;

    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("aimobs.json");
    }

    public static void loadConfig() {
        try (FileReader reader = new FileReader(getConfigPath().toFile())) {
            config = new Gson().fromJson(reader, Config.class);
            Objects.requireNonNull(config);
        } catch (Exception e) {
            config = new Config();
            saveConfig();
        }
    }
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(getConfigPath().toFile())) {
            new Gson().toJson(config, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
