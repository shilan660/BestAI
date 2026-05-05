package com.best.agent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.best.agent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ResourceDownLoadTool {

    private final String FILE_PATH = FileConstant.FILE_SAVE_DIR + "/file";
    /**
     * 通过链接下载资源到本地
     *
     * @param url 资源下载链接
     * @return 下载结果
     */
    @Tool(description = "Download a resource file from URL")
    public String downloadResource(
            @ToolParam(description = "Resource URL to download") String url
    ) {
        try {
            String downloadDir = FILE_PATH + "/downloads";

            FileUtil.mkdir(downloadDir);

            File file = HttpUtil.downloadFileFromUrl(url, downloadDir);

            return "资源下载成功，保存路径：" + file.getAbsolutePath();
        } catch (Exception e) {
            return "资源下载失败：" + e.getMessage();
        }
    }
}
