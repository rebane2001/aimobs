package com.rebane2001.aimobs;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Objects;

public class AIMobsConfig {
    // Inner class to hold configuration settings
    public static class Config {
        public boolean enabled = true; // Enable or disable the mod
        public String apiKey = ""; // OpenAI API key
        public String voiceApiKey = ""; // Voice API key (if applicable)
        public String model = "gpt-3.5-turbo-16k"; // Model to be used for conversation
        public float temperature = 0.3f; // Model temperature setting
    }

    public static Config config; // Config instance

    // Get the path to the configuration file
    private static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("aimobs.json");
    }

    // Load the configuration from the file
    public static void loadConfig() {
        try (FileReader reader = new FileReader(getConfigPath().toFile())) {
            config = new Gson().fromJson(reader, Config.class);
            Objects.requireNonNull(config); // Ensure that the config is not null
        } catch (Exception e) {
            // If an exception occurs, create a new default configuration and save it
            config = new Config();
            saveConfig();
        }
    }

    // Save the configuration to the file
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(getConfigPath().toFile())) {
            new Gson().toJson(config, writer); // Write the JSON representation of the config to the file
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
