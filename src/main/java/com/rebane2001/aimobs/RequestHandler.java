package com.rebane2001.aimobs;

import com.google.gson.Gson;
import com.jackdaw.chatWithNPC.ChatWithNPCMod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
//import java.util.Objects;
//import java.util.Objects;

public class RequestHandler {
    private static class OpenAIRequest {
        String model = "text-davinci-003";
        String stop = "\"";
        String prompt = "";
        float temperature = 0.6f;
        int max_tokens = 512;

        OpenAIRequest(String prompt, String model, float temperature) {
            this.prompt = prompt;
            this.model = model;
            this.temperature = temperature;
        }
    }

    private static class OpenAIResponse {
        static class Choice {
            String text;
        }
        Choice[] choices;
    }

    public static String getAIResponse(String prompt) throws IOException {
        if (prompt.length() > 4096) prompt = prompt.substring(prompt.length() - 4096);
        ChatWithNPCMod.LOGGER.info("Prompt: " + prompt);

        OpenAIRequest openAIRequest = new OpenAIRequest(prompt, AIMobsConfig.config.model, AIMobsConfig.config.temperature);
        String data = new Gson().toJson(openAIRequest);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpPost request = new HttpPost("https://api.openai.com/v1/completions");
            StringEntity params = new StringEntity(data, "UTF-8");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Authorization", "Bearer " + AIMobsConfig.config.apiKey);
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return new Gson().fromJson(responseString, OpenAIResponse.class).choices[0].text.replace("\n", " ");
        }
    }
}
