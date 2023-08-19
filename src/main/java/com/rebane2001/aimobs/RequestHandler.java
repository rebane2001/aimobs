package com.rebane2001.aimobs;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.ByteArrayOutputStream;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class RequestHandler {
     public static class Message {
        String role;
        String content;

        // Constructor that takes role and content
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

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





    public static String getTranscription() throws IOException {
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        URL url = new URL("https://api.openai.com/v1/audio/transcriptions");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + AIMobsConfig.config.apiKey);
        connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        connection.setDoOutput(true);

        // Read the file into a ByteArrayOutputStream for the HTTP request
        ByteArrayOutputStream requestBytes = new ByteArrayOutputStream();
        File inputFile = new File("audio.wav");
        try (InputStream fileInputStream = new FileInputStream(inputFile);
            OutputStream os = requestBytes;
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), true)) {
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"audio.wav\"").append("\r\n");
            writer.append("Content-Type: audio/wav").append("\r\n");
            writer.append("\r\n").flush();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();

            writer.append("\r\n").flush();
            writer.append("--" + boundary).append("\r\n");
            writer.append("Content-Disposition: form-data; name=\"model\"").append("\r\n");
            writer.append("\r\n").append("whisper-1").append("\r\n");
            writer.append("--" + boundary + "--").append("\r\n").flush();
        }

        // Print the request headers
        System.out.println("Request method: " + connection.getRequestMethod());
        for (String header : connection.getRequestProperties().keySet()) {
            System.out.println(header + ": " + connection.getRequestProperty(header));
        }

        // Write the request body to the actual connection output stream
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestBytes.toByteArray());
        }

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

        System.out.println("Response from OpenAI Whisper: " + response);

        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);
        return jsonResponse.get("text").getAsString();
    }









    public static String getAIResponse(Message[] messages) throws IOException {
        OpenAIRequest openAIRequest = new OpenAIRequest(messages);
        String data = new Gson().toJson(openAIRequest);
        System.out.println("Query to OpenAI: " + data);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("https://api.openai.com/v1/chat/completions");
            StringEntity params = new StringEntity(data, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Bearer " + AIMobsConfig.config.apiKey);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            String responseText = "";
            System.out.println("Response from OpenAI: " + responseString);
            OpenAIResponse responseObj = new Gson().fromJson(responseString, OpenAIResponse.class);

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
