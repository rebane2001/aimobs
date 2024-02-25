package com.jackdaw.chatwithnpc.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OpenAIHandler {

    private static final String url = "https://api.openai.com/v1/chat/completions";
    private static String apiKey = "";
    private static String model = "gpt-3.5-turbo";

    private static final String maxTokens = "4096";

    public static void updateSetting() {
        apiKey = SettingManager.apiKey;
        model = SettingManager.model;
    }

    private static String json_message(String prompt) {
        return "{" +
                "\"model\": \"" + model + "\", " +
                "\"messages\": [{"
                + "\"role\": \"system\", "
                + "\"content\": \"" + prompt + "\""
                + "}], \"max_tokens\": " + maxTokens + "}";
    }

    public static String encodeString(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * Send a request to the OpenAI API
     * @param prompt The prompt to send
     * @return The response from the API
     * @throws Exception If the request fails
     */
    public static String sendRequest(String prompt) throws Exception {
        if (prompt.length() > 8192) prompt = prompt.substring(prompt.length() - 8192);
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);

            // 设置请求头
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            // 构建请求体
            request.setEntity(new StringEntity(json_message(encodeString(prompt.replace("\n", " ")))));
            ChatWithNPCMod.LOGGER.info("[chat-with-npc] Draft Request: \n" + json_message(encodeString(prompt.replace("\n", " "))));

            try (CloseableHttpResponse response = client.execute(request)) {
                String res =  EntityUtils.toString(response.getEntity());
                ChatWithNPCMod.LOGGER.info("[chat-with-npc] Draft Response: \n" + res);
                JsonObject jsonObject = JsonParser.parseString(res).getAsJsonObject();
                return jsonObject.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
            }
        }
    }

}

