package com.best.agent.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.best.agent.agent.BestManus;
import com.best.agent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private LoveApp loveApp;

    @Resource
    private DashScopeChatModel dashScopeChatModel;

    @Resource
    private ToolCallback[] allTools;


//    /**
//     * 同步调用AI大师应用
//     * @param message 用户信息
//     * @param chatId 对话Id
//     * @return AI返回内容
//     */
//    @GetMapping(value = "/love_app/chat/sse")
//    public String doChatWithLoveAppSync(String message , String chatId) {
//         return loveApp.doChat(message, chatId);
//    }

//    /**
//     * 返回AI大师应用的SSE流内容 , 直接更改返回格式 , 加注解
//     * @param message
//     * @param chatId
//     * @return
//     */
//    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> doChatWithLoveAppSSE(String message , String chatId) {
//        return loveApp.doChatByStream(message, chatId);
//    }

//    /**
//     * 用serverSentEvent拼接返回 , 把每一项用serverSentEvent包装起来
//     * @param message
//     * @param chatId
//     * @return
//     */
//    @GetMapping(value = "/love_app/chat/sse")
//    public Flux<ServerSentEvent<String>> doChatWithLoveAppSSEPlus(String message , String chatId) {
//        return loveApp.doChatByStream(message, chatId)
//                .map(chunk -> ServerSentEvent.<String>builder()
//                        .data(chunk)
//                        .build());
//    }

    /**
     * 使用sseEmitter返回
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/love_app/chat/sse")
    public SseEmitter doChatWithLoveApp(String message , String chatId) {
        SseEmitter emitter = new SseEmitter();
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        chunk ->
                        {
                            try {
                                emitter.send(chunk);
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                            }
                        }
//                        错误处理
                        , emitter::completeWithError
//                        完成处理
                        , emitter::complete
                );
        return emitter;
    }

    @GetMapping("manus/chat")
    public SseEmitter doChatWithManus(String message) {
        BestManus bestManus = new BestManus(allTools , dashScopeChatModel);
        return bestManus.runByStream(message);
    }

}
