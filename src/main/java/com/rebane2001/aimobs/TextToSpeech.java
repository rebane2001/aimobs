package com.rebane2001.aimobs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TextToSpeech {
    private static VoiceManager voiceManager = new VoiceManager("/voices.json");
    

    // Synthesize and play text-to-speech for given text and mob UUID
    public static void synthesizeAndPlay(String gptResponseText, UUID mobUUID) {
        System.out.println("TextToSpeech");
        Voice voice = voiceManager.getVoiceForMob(mobUUID);
        String payload = createPayload(gptResponseText, voice);

        try {
            String url = "https://texttospeech.googleapis.com/v1beta1/text:synthesize?key=" + AIMobsConfig.config.voiceApiKey;

            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                HttpPost request = new HttpPost(url);
                StringEntity params = new StringEntity(payload, "UTF-8");
                request.addHeader("Content-Type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                // Extract the base64 audio content
                String base64Audio = extractBase64Audio(responseString);
                playSound(base64Audio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Create a JSON payload for the text-to-speech request
    private static String createPayload(String text, Voice voice) {
        JsonObject payload = new JsonObject();
        JsonObject input = new JsonObject();
        input.addProperty("text", text);
        JsonObject voiceObj = new JsonObject();
        voiceObj.addProperty("languageCode", voice.getLanguageCodes().get(0)); // Assuming the first language code
        voiceObj.addProperty("name", voice.getName());
        voiceObj.addProperty("ssmlGender", voice.getSsmlGender());
        JsonObject audioConfig = new JsonObject();
        audioConfig.addProperty("audioEncoding", "LINEAR16"); // WAV encoding
        payload.add("input", input);
        payload.add("voice", voiceObj);
        payload.add("audioConfig", audioConfig);

        return payload.toString();
    }

    // Extract the base64 audio content from the response JSON
    private static String extractBase64Audio(String responseString) {
        JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
        return responseJson.get("audioContent").getAsString();
    }

    // Play the sound from a base64 encoded string
    public static void playSound(String base64Sound) {
        try {
            // Decode the base64 string to a byte array
            byte[] soundBytes = Base64.getDecoder().decode(base64Sound);
            // Convert byte array to an InputStream
            InputStream byteArrayInputStream = new ByteArrayInputStream(soundBytes);
            // Wrap the InputStream in a BufferedInputStream
            BufferedInputStream bufferedStream = new BufferedInputStream(byteArrayInputStream);
            // Open an input stream from the BufferedInputStream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
            // Close the audio input stream
            audioInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
