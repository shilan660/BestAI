package com.best.agent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

@Component
public class TerminalOperationTool {

    @Tool(description = "Execute a terminal command on Windows and return the output")
    public String executeCommand(
            @ToolParam(description = "Command to execute, for example: dir, python test.py") String command) {

//        预处理
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
        processBuilder.redirectErrorStream(true);

        StringBuilder output = new StringBuilder();

        try {
//            执行命令
            Process process = processBuilder.start();
//            等待命令执行完成(30s超时)
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), Charset.forName("GBK")))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
            }

            if (!finished) {
                process.destroyForcibly();
                return "命令执行超时，已终止：" + command;
            }

            int exitCode = process.exitValue();

            return """
                    命令：%s
                    退出码：%s
                    输出：
                    %s
                    """.formatted(command, exitCode, output);

        } catch (Exception e) {
            return "执行命令失败：" + e.getMessage();
        }
    }
}
