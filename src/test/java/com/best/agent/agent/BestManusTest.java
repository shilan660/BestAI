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
                我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点，
                并结合一些网络图片，制定一份详细的约会计划，
                并以 PDF 格式输出""";
//        String Prompt = """
//            把"你好"转换为PDF格式输出 ,用我给你的pdf生成工具
//            """;
        String answer = bestManus.run(Prompt);
        Assertions.assertNotNull(answer);
    }
}