package com.best.agent.tools;

import cn.hutool.json.JSONUtil;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class WebSearchTool {

    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String API_KEY;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    public WebSearchTool(String api_key) {
        this.API_KEY = api_key;
    }

    @Tool(description = "Search web pages from Baidu and return search results")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {

        HttpUrl url = HttpUrl.parse(SEARCH_API_URL).newBuilder()
                .addQueryParameter("engine", "baidu")
                .addQueryParameter("q", query)
                .addQueryParameter("api_key", API_KEY)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "搜索失败，HTTP 状态码：" + response.code();
            }

            String body = response.body().string();

            // 简单校验 JSON
            if (!JSONUtil.isTypeJSON(body)) {
                return "搜索失败，返回内容不是 JSON：" + body;
            }
            return body;

        } catch (IOException e) {
            return "搜索异常：" + e.getMessage();
        }
    }
}
