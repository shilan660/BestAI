package com.best.agent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.best.agent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class FileOperationTool {

    private final String FILE_PATH = FileConstant.FILE_SAVE_DIR + "/file";


    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of a file to read") String fileName) {
        String filePath = FILE_PATH + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (IORuntimeException e) {
            return "Error reading file :" + filePath ;
        }

    }

    @Tool(description = "Write content to a file")
    public String writeFile(
            @ToolParam(description = "Name of a file to write") String fileName,
            @ToolParam(description = "Content to write to the file") String content) {
        String filePath = FILE_PATH + "/" + fileName;
        try {
            FileUtil.mkdir(filePath);
            FileUtil.writeUtf8String(content , filePath);
            return filePath;
        }catch (IORuntimeException e){
            return "Error writing file :" + filePath;
        }
    }
}
