package com.yuqiqi.superagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

public class HttpAiInvoke {

    public static void main(String[] args) {
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
        String apiKey = TestApiKey.API_KEY; // 请替换

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "qwen3-max-2025-09-23");

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "You are a helpful assistant.");

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", "你是谁？");

        JSONObject messages = new JSONObject();
        messages.put("messages", new Object[]{systemMsg, userMsg});

        JSONObject input = new JSONObject();
        input.put("messages", new Object[]{systemMsg, userMsg});

        JSONObject parameters = new JSONObject();
        parameters.put("result_format", "message");

        requestBody.put("input", input);
        requestBody.put("parameters", parameters);

        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(20000)
                .execute();

        System.out.println("Response: " + response.body());
    }
}
