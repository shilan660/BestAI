package com.best.agent.agent;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.best.agent.agent.model.AgentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ToolCallAgent extends ReActAgent {

//    可用的工具
    private final ToolCallback[] availableTools;

//     存储工具调用的返回结果
    private ChatResponse toolCallChatResponse;

//    工具管理者
    private final ToolCallingManager toolCallingManager;

//    会话
    private final ChatOptions chatOptions;

//    自己管理工具调用,取消spring自动调用
    public ToolCallAgent(ToolCallback[] availableTools) {
        super();
        this.availableTools = availableTools;
        this.toolCallingManager = ToolCallingManager.builder().build();

        this.chatOptions = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
    }

    @Override
    public boolean think() {
//        1. 校验提示词,拼接用户提示词 userMessage
        if (StrUtil.isNotBlank(getNextStepPrompt())) {
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
//        2. 调用 AI 大模型,获取工具调用结果
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList , chatOptions);

        try {
            ChatResponse chatResponse = getChatClient()
                    .prompt(prompt)
                    .system(getSystemPrompt())
                    .toolCallbacks(availableTools)
                    .call()
                    .chatResponse();
            this.toolCallChatResponse = chatResponse;
//        3. 处理回复,提取工具调用信息
//        取出大模型真正的回复
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            List<AssistantMessage.ToolCall> toolcallList = assistantMessage.getToolCalls();
//        输出提示信息
            String result = assistantMessage.getText();
            log.info(getName() + "的思考" + result);
            log.info(getName() + "选择了" + toolcallList.size() + "个工具");

            String tooCallInfo = toolcallList.stream()
                    .map(toolCall -> String.format("工具调用: %s, 参数: %s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info("工具调用信息: {}", tooCallInfo);

            if (toolcallList == null || toolcallList.isEmpty()) {
                return false;
            }else {
                return true;
            }

//        异常处理
        } catch (Exception e) {

            log.error(getName() + "的思考过程遇到了问题: " + e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到错误: " + e.getMessage()));

            return false;
        }
    }

    @Override
    public String act() {

        if (!toolCallChatResponse.hasToolCalls()) {
            return "暂无工具调用";
        }

//        获取prompt
        Prompt prompt = new Prompt(getMessageList() , chatOptions);
//        调用工具toolexecutionresult
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt , toolCallChatResponse);
//        把工具调用结果(消息上下文)放到messageList conversationHistory 取到最近一条工具调用信息
        setMessageList(toolExecutionResult.conversationHistory());

//        获取工具调用信息(lastest)
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());

//        判断是否调用中止工具
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                .anyMatch(response -> "doTerminal".equals(response.name()));

        if  (terminateToolCalled) {
//            更改智能体工作状态
            setState(AgentState.FINISHED);
            return "已结束";
        }
//        拼接返回结果(stream)
        String result = toolResponseMessage.getResponses().stream()
                .map(response -> "工具" + response.name() + "的返回结果为" + response.responseData())
                .collect(Collectors.joining("\n"));
        log.info(result);
//        返回
        return result;
    }

}
