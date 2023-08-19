package com.rebane2001.aimobs;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class VoiceManager {
    private List<Voice> voices;
    private HashMap<UUID, Voice> mobVoices;
    private Random random;
    private static final String MOB_VOICES_FILE_PATH = "mob_voices.json";

    public VoiceManager(String voiceFilePath) {
        this.random = new Random();
        this.mobVoices = new HashMap<>();
        loadVoices(voiceFilePath);
        loadMobVoices();
    }

    private void loadVoices(String voiceFilePath) {
        try (Reader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(voiceFilePath), StandardCharsets.UTF_8))) {
            voices = new Gson().fromJson(reader, new TypeToken<List<Voice>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadMobVoices() {
        try (FileReader reader = new FileReader(MOB_VOICES_FILE_PATH)) {
            Gson gson = new Gson();
            Type mobVoicesType = new TypeToken<HashMap<UUID, Voice>>() {}.getType();
            HashMap<UUID, Voice> loadedMobVoices = gson.fromJson(reader, mobVoicesType);
            if (loadedMobVoices != null) {
                mobVoices = loadedMobVoices;
            } else {
                mobVoices = new HashMap<>();
            }
        } catch (IOException e) {
            mobVoices = new HashMap<>(); // Initialize mobVoices if failed to load
            saveMobVoices(); // Create and save an empty file
            e.printStackTrace();
        }
    }

    private void saveMobVoices() {
        try (FileWriter writer = new FileWriter(MOB_VOICES_FILE_PATH)) {
            new Gson().toJson(mobVoices, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private Voice getRandomVoice() {
        return voices.get(random.nextInt(voices.size()));
    }
}
