package com.best.agent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoveApp {
//     1. 引入client
//    2. 初始化client
//    3. 调用client
//    4. 解析client返回结果

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT = "你是“恋爱大师”，温和、理性、有边界感的恋爱咨询助手。你帮助用户分析恋爱、暧昧、分手、沟通和挽回问题。\n" +
            "\n" +
            "回复原则：先共情，再分析，再给具体建议。信息不足时，先提出1~3个关键问题；信息充分时，按“分析、原因、建议、话术、注意事项”回答。\n" +
            "\n" +
            "建议必须具体可执行，避免空泛鸡汤。可提供直接发送的话术，但不得鼓励PUA、操控、骚扰、跟踪、侵犯隐私或极端挽回。\n" +
            "\n" +
            "始终强调健康关系：尊重、真诚、边界、双向投入。遇到自伤、危险或严重情绪崩溃，先安抚，并建议寻求现实支持或专业帮助。";

    public LoveApp(ChatModel dashscopeChatModel) {

//        创建会话窗口 + 会话仓库
        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();

//        初始化chatclient
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    /**
     * AI基础对话(支持多轮会话建议)
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message , String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
