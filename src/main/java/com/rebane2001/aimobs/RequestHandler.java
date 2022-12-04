package com.rebane2001.aimobs;

import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

public class RequestHandler {
    private static class OpenAIRequest {
        String model = "text-davinci-002";
        String stop = "\"";
        String prompt = "";
        float temperature = 0.6f;
        int max_tokens = 64;

        OpenAIRequest(String prompt) {
            this.prompt = prompt;
        }
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
        AIMobsMod.LOGGER.info("Prompt: " + prompt);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OpenAIRequest openAIRequest = new OpenAIRequest(prompt, AIMobsConfig.config.model, AIMobsConfig.config.temperature);
        String data = new Gson().toJson(openAIRequest);

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(data, JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .addHeader("Authorization", "Bearer " + AIMobsConfig.config.apiKey)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        return new Gson().fromJson(Objects.requireNonNull(response.body()).string(), OpenAIResponse.class).choices[0].text.replace("\n", " ");
    }
}
