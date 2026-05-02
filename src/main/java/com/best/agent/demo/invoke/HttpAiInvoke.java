package com.best.agent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * 阿里云灵积AI HTTP调用
 * */
public class HttpAiInvoke {
    public static void main(String[] args) {
        String apiKey = TestApiKey.API_KEY;

        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        String jsonBody = """
            {
                "model": "qwen-plus",
                "input": {
                    "messages": [
                        {
                            "role": "system",
                            "content": "You are a helpful assistant."
                        },
                        {
                            "role": "user",
                            "content": "你是谁？"
                        }
                    ]
                },
                "parameters": {
                    "result_format": "message"
                }
            }
            """;

        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(jsonBody)
                .execute();

        System.out.println(response.getStatus());
        System.out.println(response.body());
    }
}
