package com.best.bestimagesearchmcpserver.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class ImageSearchTool{

    private final String apiKey;

    public ImageSearchTool(@Value("${search_api.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search images from Pexels by keyword")
    public String searchImage(
            @ToolParam(description = "Image search keyword")
            String query
    ) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            String url = "https://api.pexels.com/v1/search"
                    + "?query=" + encodedQuery
                    + "&per_page=5"
                    + "&page=1";

            String response = HttpRequest.get(url)
                    .header("Authorization", apiKey)
                    .execute()
                    .body();

            JSONObject jsonObject = JSONUtil.parseObj(response);
            JSONArray photos = jsonObject.getJSONArray("photos");

            if (photos == null || photos.isEmpty()) {
                return "没有找到相关图片";
            }

            StringBuilder result = new StringBuilder();
            result.append("找到以下图片：\n");

            for (int i = 0; i < photos.size(); i++) {
                JSONObject photo = photos.getJSONObject(i);
                JSONObject src = photo.getJSONObject("src");

                result.append("\n")
                        .append(i + 1).append(". ")
                        .append("摄影师：").append(photo.getStr("photographer")).append("\n")
                        .append("图片地址：").append(photo.getStr("url")).append("\n")
                        .append("原图：").append(src.getStr("original")).append("\n")
                        .append("中等尺寸：").append(src.getStr("medium")).append("\n")
                        .append("描述：").append(photo.getStr("alt")).append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "图片搜索失败：" + e.getMessage();
        }
    }
}
