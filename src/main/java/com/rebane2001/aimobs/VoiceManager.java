package com.rebane2001.aimobs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.util.UUID;

public class VoiceManager {

    // Custom UUID type adapter for Gson
    private static class UUIDTypeAdapter extends TypeAdapter<UUID> {
        @Override
        public void write(JsonWriter out, UUID value) throws IOException {
            out.value(value != null ? value.toString() : null);
        }

        @Override
        public UUID read(JsonReader in) throws IOException {
            try {
                return UUID.fromString(in.nextString());
            } catch (IllegalArgumentException e) {
                // Handle invalid UUID here if necessary
                return null;
            }
        }
    }



    private List<Voice> voices; // List of available voices
    private HashMap<UUID, Voice> mobVoices; // Map of mob UUIDs to their assigned voices
    private Random random; // Random generator for voice selection
    private static final String MOB_VOICES_FILE_PATH = "mob_voices.json"; // File path for mob voices data

    public VoiceManager(String voiceFilePath) {
        this.random = new Random();
        this.mobVoices = new HashMap<>();
        loadVoices(voiceFilePath); // Load available voices
        loadMobVoices(); // Load previously assigned mob voices
    }

    // Loads voices from a JSON file
    private void loadVoices(String voiceFilePath) {
        try (Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(voiceFilePath), StandardCharsets.UTF_8))) {
            voices = new Gson().fromJson(reader, new TypeToken<List<Voice>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads previously assigned mob voices from a JSON file
    private void loadMobVoices() {
        try (FileReader reader = new FileReader(MOB_VOICES_FILE_PATH)) {
            Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
            Type mobVoicesType = new TypeToken<HashMap<UUID, Voice>>() {}.getType();
            HashMap<UUID, Voice> loadedMobVoices = gson.fromJson(reader, mobVoicesType);
            mobVoices = loadedMobVoices != null ? loadedMobVoices : new HashMap<>();
        } catch (IOException e) {
            mobVoices = new HashMap<>();
            saveMobVoices(); // Create and save an empty file if failed to load
            e.printStackTrace();
        }
    }

    // Saves the current mob voices to a JSON file
    private void saveMobVoices() {
        try (FileWriter writer = new FileWriter(MOB_VOICES_FILE_PATH)) {
            new Gson().toJson(mobVoices, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrieves the assigned voice for a mob or assigns and saves a new random voice if none exists
    public Voice getVoiceForMob(UUID mobUUID) {
        if (mobVoices.containsKey(mobUUID)) {
            return mobVoices.get(mobUUID);
        } else {
            Voice voice = getRandomVoice();
            mobVoices.put(mobUUID, voice);
            saveMobVoices(); // Save every time a new voice is assigned
            return voice;
        }
    }

    // Returns a random voice from the available voices list
    private Voice getRandomVoice() {
        return voices.get(random.nextInt(voices.size()));
    }
}
