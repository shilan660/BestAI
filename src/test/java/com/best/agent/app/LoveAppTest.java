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



//    @Test
//    void doChat() {
//        String chatId = UUID.randomUUID().toString();
//
//        String message = "你好,我叫时兰,我喜欢菲伦";
//        LoveApp.Tests answer = loveApp.doChat(message , chatId);
//        Assertions.assertNotNull(answer);
////
////        message = "我不知道怎么跟她讲话";
////        answer = loveApp.doChat(message , chatId);
////        Assertions.assertNotNull(answer);
////
////        message = "我喜欢的人叫什么名字";
////        answer = loveApp.doChat(message , chatId);
////        Assertions.assertNotNull(answer);
//
//    }
//
//    @Test
//    void doChatWithReport() {
//        String chatId = UUID.randomUUID().toString();
//        String message = "你好,我叫时兰,我喜欢菲伦,但是我不知道应该怎么追求她,你能帮我想一些建议吗";
//        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message , chatId);
//        Assertions.assertNotNull(loveReport);
//        Assertions.assertNotNull(loveReport.suggestions());
//
//    }

    @Test
    void doChatWithRag(){
        String chatId = UUID.randomUUID().toString();
        String message = "我去相亲了,但是我不知道该不该跟她深入发展";
        String response = loveApp.doChatWithRag(message , chatId);
        Assertions.assertNotNull(response);
    }

    @Test
    void doChatWithTools() {

        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");


        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");


        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");


        testMessage("执行 Python3 脚本来生成数据分析报告");


        testMessage("保存我的恋爱档案为文件");


        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp(){
        String chatId = UUID.randomUUID().toString();
//        String message = "我的另一半居住在武汉江汉区,帮我找五公里内合适的约会地点 , 记得要给我图片";
//        String response = loveApp.doChatWithMcp(message , chatId);

        String message = "帮我搜索一些关于包子的图片";
        String response = loveApp.doChatWithMcp(message, chatId);

        Assertions.assertNotNull(response);
    }

}