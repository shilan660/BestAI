package com.best.agent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BestManusTest {
    @Resource
    private BestManus bestManus;


    @Test
    void test() {
        String Prompt = """
                你好""";
//        String Prompt = """
//            把"你好"转换为PDF格式输出 ,用我给你的pdf生成工具
//            """;
        String answer = bestManus.run(Prompt);
        Assertions.assertNotNull(answer);
    }
}