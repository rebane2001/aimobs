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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class TextToSpeech {

    public static void synthesizeAndPlay(String gptResponseText) {
        try {
            String url = "https://texttospeech.googleapis.com/v1beta1/text:synthesize?key=" + AIMobsConfig.config.voiceApiKey;
            String payload = createPayload(gptResponseText);

            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                HttpPost request = new HttpPost(url);
                StringEntity params = new StringEntity(payload, "UTF-8");
                request.addHeader("Content-Type", "application/json");
                request.setEntity(params);
                HttpResponse response = httpClient.execute(request);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");
                //System.out.println("Response from Google TTS: " + responseString);

                
                // Extract the base64 audio content
                String base64Audio = extractBase64Audio(responseString);
                //System.out.println("Extracted Base64 Audio: " + base64Audio);

                playSound(base64Audio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String createPayload(String text) {
        JsonObject input = new JsonObject();
        input.addProperty("text", text);

        JsonObject voice = new JsonObject();
        voice.addProperty("languageCode", "en-gb");
        voice.addProperty("name", "en-GB-Standard-A");
        voice.addProperty("ssmlGender", "FEMALE");

        JsonObject audioConfig = new JsonObject();
        audioConfig.addProperty("audioEncoding", "LINEAR16");

        JsonObject payload = new JsonObject();
        payload.add("input", input);
        payload.add("voice", voice);
        payload.add("audioConfig", audioConfig);

        return payload.toString();
    }

    private static String extractBase64Audio(String responseString) {
        JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
        return responseJson.get("audioContent").getAsString();
    }


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
