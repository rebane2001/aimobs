package com.rebane2001.aimobs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RequestHandler {
    // Nested class to represent a message for OpenAI
    public static class Message {
        String role;
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    // Nested classes to represent OpenAI request and response structure
    private static class OpenAIRequest {
        String model = "gpt-3.5-turbo-16k";
        Integer max_tokens = 128;
        Message[] messages;

        OpenAIRequest(Message[] messages) {
            this.messages = messages;
        }
    }

    private static class OpenAIResponse {
        static class Choice {
            static class Message {
                String role;
                String content;
            }
            Message message;
        }
        Choice[] choices;
    }

    public static String getTranscription(InputStream audioInputStream) throws IOException {
        // Boundary for the multipart/form-data request
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        URL url = new URL("https://api.openai.com/v1/audio/transcriptions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + AIMobsConfig.config.apiKey);
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        connection.setDoOutput(true);

        // Build the request body using the audioInputStream
        ByteArrayOutputStream requestBytes = new ByteArrayOutputStream();
        try (OutputStream os = requestBytes;
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"").append("\r\n");
            writer.append("Content-Type: audio/wav").append("\r\n");
            writer.append("\r\n").flush();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
            writer.append("\r\n").flush();
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"model\"").append("\r\n");
            writer.append("\r\n").append("whisper-1").append("\r\n");
            writer.append("--" + boundary + "--").append("\r\n").flush();
        }

        // Write the request body to the connection
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBytes.toByteArray());
        }

        // Read the response from OpenAI
        StringBuilder response = new StringBuilder();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            InputStream errorStream = connection.getErrorStream();
            String errorResponse = new BufferedReader(new InputStreamReader(errorStream))
                    .lines().collect(Collectors.joining("\n"));
            System.err.println("Error response from OpenAI Whisper: " + errorResponse);
            throw new IOException("Server returned HTTP response code: " + responseCode);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        // Parse the response and return the transcription
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        return jsonResponse.get("text").getAsString();
    }




    // Method to get AI response from OpenAI
    public static String getAIResponse(Message[] messages) throws IOException {
        OpenAIRequest openAIRequest = new OpenAIRequest(messages);
        String data = new Gson().toJson(openAIRequest);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("https://api.openai.com/v1/chat/completions");
            StringEntity params = new StringEntity(data, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Bearer " + AIMobsConfig.config.apiKey);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            // Parse the response
            OpenAIResponse responseObj = new Gson().fromJson(responseString, OpenAIResponse.class);
            String responseText = "";
            if (responseObj.choices != null) {
                boolean allChoicesNull = true;
                for (OpenAIResponse.Choice choice : responseObj.choices) {
                    if (choice.message.content != null) {
                        allChoicesNull = false;
                        responseText = choice.message.content.replace("\\n", "");
                        break;
                    }
                }
                if (allChoicesNull) {
                    responseText = "Sorry, I didn't understand your message.";
                }
            } else {
                responseText = "Unexpected response structure.";
            }
            return responseText;
        }
    }
}
