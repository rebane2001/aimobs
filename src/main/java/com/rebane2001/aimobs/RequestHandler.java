package com.rebane2001.aimobs;

import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
 
import java.io.IOException;

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
        Integer max_tokens = 64;
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
