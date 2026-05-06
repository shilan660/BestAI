package com.best.agent.app;

import com.best.agent.advisor.MyLoggerAdvisor;
import com.best.agent.chatmemory.FileBasedChatMemory;
import com.best.agent.rag.LoveAppContextualQueryAugmenterFactory;
import com.best.agent.rag.LoveAppRagCustomAdvisorFactory;
import com.best.agent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class LoveApp {

    @Resource
    private SimpleVectorStore simpleStore;

    @Resource
    private Advisor LoveAppRagCloudAdvisor;

    @Resource
    private QueryRewriter queryRewriter;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    @Resource
    private VectorStore PgVectorStore;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    LoveAppContextualQueryAugmenterFactory loveAppContextualQueryAugmenterFactory;

//    自动读取对应工具能力
//    @Resource
//    private ToolCallbackProvider toolCallbackProvider;

    @Resource
    private SyncMcpToolCallbackProvider toolCallbackProvider;

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

    private final String REPORT_PROMPT = "每次对话后都要生成恋爱报告,标题为{用户名}的恋爱报告,内容为建议列表";

    private final String TEST_PROMPT = "请根据用户的情感问题，从【分析】【原因】【建议】【话术】【注意事项】五个部分进行回复，内容具体、温和、有边界感，并尽量给出可直接使用的话术。";

    private final String TOOL_PROMPT = "当用户询问最新信息、网站内容、网页资料、下载、生成文件、执行脚本时，必须优先调用工具，不允许凭空猜测。";

    @Autowired
    private VectorStore vectorStore;

    record LoveReport(String title, List<String> suggestions) {

    }
    record Tests(String analysis, String reason, String suggestion, String phrase, String notice){

    }
    public LoveApp(ChatModel dashscopeChatModel) {

//        初始化基于文件的对话记忆
//        指定目录
        String fileDir = System.getProperty("user.dir") + "/tem/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

//        初始化基于内存的对话记忆
//        创建会话窗口 + 会话仓库
//        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();
//
//        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .chatMemoryRepository(repository)
//                .maxMessages(10)
//                .build();

//        初始化chatClient
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * AI基础对话(支持多轮会话建议)
     * @param message 用户输入内容
     * @param chatId 会话ID（用于上下文记忆）
     * @return 模型回复内容
     */
    public Tests doChat(String message , String chatId) {
        Tests test = chatClient
                .prompt()
                .system(TEST_PROMPT)
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
                .call()
                .entity(Tests.class);
        log.info("test: {}", test);
        return test;
    }
    /**
     * 生成恋爱报告
     * @param message 用户输入内容
     * @param chatId 会话ID（用于上下文记忆）
     * @return 模型回复内容
     */
    public LoveReport doChatWithReport(String message , String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(REPORT_PROMPT)
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
//                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
                .call()

                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    public String doChatWithRag(String message , String chatId) {
//        String newQuery = queryRewriter.doQueryRewriter(message);

        ChatResponse response = chatClient
                .prompt()
                .system(TEST_PROMPT)
                .user(message)
//                日志记录
                .advisors(new MyLoggerAdvisor())
//                RAG
//                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
//                应用RAG检索增强服务
                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(simpleStore , "已婚"))
//                .advisors(LoveAppRagCloudAdvisor)
                .call()
                .chatResponse();
        log.info("response: {}", response);
        return response.toString();
    }

//    调用工具能力
    public String doChatWithTools(String message , String chatId) {
        String answer = chatClient
                .prompt()
                .system(TOOL_PROMPT)
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
    //                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
//                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(allTools)
                .call()
                .content();
        log.info("answer: {}", answer);
        return answer;
    }
//    MCP 调用工具能力
    public String doChatWithMcp(String message , String chatId) {
        String answer = chatClient
                .prompt()
                .system(TOOL_PROMPT)
                .user(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, chatId))
                //                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
//                .advisors(QuestionAnswerAdvisor.builder(simpleStore).build())
                .advisors(new MyLoggerAdvisor())
                .toolCallbacks(toolCallbackProvider)
                .call()
                .content();
        log.info("answer: {}", answer);
        return answer;
    }

}
