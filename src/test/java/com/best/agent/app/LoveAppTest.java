package com.best.agent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好,我叫时兰,我喜欢菲伦";
        String answer = loveApp.doChat(message , chatId);
        Assertions.assertNotNull(answer);

        message = "我不知道怎么跟她讲话";
        answer = loveApp.doChat(message , chatId);
        Assertions.assertNotNull(answer);

        message = "我喜欢的人叫什么名字";
        answer = loveApp.doChat(message , chatId);
        Assertions.assertNotNull(answer);

    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好,我叫时兰,我喜欢菲伦,但是我不知道应该怎么追求她,你能帮我想一些建议吗";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message , chatId);
        Assertions.assertNotNull(loveReport);
        Assertions.assertNotNull(loveReport.suggestions());

    }
}