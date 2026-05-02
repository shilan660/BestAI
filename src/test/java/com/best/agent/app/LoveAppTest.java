package com.best.agent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void doChat() {
        String chatId = UUID.randomUUID().toString();

        String message = "你好,我叫时兰";
        String answer = loveApp.doChat(message , chatId);
        Assertions.assertNotNull(answer);

        message = "你好,我19岁";
        answer = loveApp.doChat(message , chatId);
        Assertions.assertNotNull(answer);

        message = "我叫什么,我多少岁";
        answer = loveApp.doChat(message , chatId);
        Assertions.assertNotNull(answer);

    }
}