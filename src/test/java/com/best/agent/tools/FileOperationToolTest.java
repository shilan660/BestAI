package com.best.agent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "我永远喜欢祖娅纳惜";
        String result = fileOperationTool.readFile(fileName);
        System.out.println(result);
        assertNotNull(result);
    }

    @Test
    void writeFile() {
        FileOperationTool tool = new FileOperationTool();
        String fileName = "我永远喜欢祖娅纳惜";
        String content = "祖娅纳惜唱歌真好听";
        String result = tool.writeFile(fileName, content);
        assertNotNull(result);
    }
}