package com.best.agent.agent;

// 基础配置类,实现状态管理,模板等

import cn.hutool.core.util.StrUtil;
import com.best.agent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Slf4j
public class BaseAgent {

//    智能体基础信息
    private String name;

//    提示词
    private String systemPrompt;
    private String nextStepPrompt;

//    代理状态
    private AgentState state = AgentState.IDLE;

//    执行步骤控制
    private int currentStep = 0;
    private int maxSteps = 10;

//    LLM大模型
    private ChatClient chatClient;

//  Memory 记忆
    private List<Message> messageList = new ArrayList<>();

//    执行代码

    /**
     * 执行任务(循环执行)
     * @param userPrompt 用户提示词
     * @return 大模型回复(
     */
    public String run(String userPrompt) {
//        异常判断
        if (state != AgentState.IDLE) {
            throw new RuntimeException("Cannot run agent when state is " + state);
        }
        if (StrUtil.isBlank(userPrompt)) {
            throw new IllegalArgumentException("userPrompt is blank");
        }

//        开始运行
        state = AgentState.RUNNING;
//        result列表
        List<String> resultList = new ArrayList<String>();

        messageList.add(new UserMessage(userPrompt));

//        for循环
        try {
            for (int i = 0 ; i < maxSteps && state != AgentState.FINISHED; i++ ) {
                currentStep = i + 1;
                log.info("Progress: {}/{}", currentStep, maxSteps);

                String stepResult = step();
                String result = "now step " + currentStep + " , " + stepResult;
                resultList.add(result);
                if ("思考完成,不需要执行".contains(stepResult)) {
                    state = AgentState.FINISHED;
                }
                if (currentStep >= maxSteps) {
                    state = AgentState.FINISHED;
                    resultList.add("Terminal Agent reached max steps" + maxSteps);
                }
            }
            log.info("resultList: {}", resultList);
        }catch (Exception e) {
                state = AgentState.ERROR;
                resultList.add("Error: " + e.getMessage());
                return "Progress Failed" + e.getMessage();
        } finally {
                clearCache();
        }

        return String.join("\n", resultList);

    }


    /**
     * 执行任务 , 流式输出
     * @param userPrompt 用户提示词
     * @return 大模型回复(
     */
    public SseEmitter runByStream(String userPrompt) {
        SseEmitter sendEmitter = new SseEmitter(300000L);
//        异常判断
//        使用异步处理,避免阻塞主线程
        CompletableFuture.runAsync(() ->{
            try {
                if (state != AgentState.IDLE) {
                    sendEmitter.send("抱歉,无法从当前状态下运行 " + state);
                    sendEmitter.complete();
                    return;
                }
                if (StrUtil.isBlank(userPrompt)) {
                    sendEmitter.send("请检查提示词不能为空");
                    sendEmitter.complete();
                    return;
                }
            } catch (IOException e) {
                setState(AgentState.ERROR);
                sendEmitter.completeWithError(e);
                return;
            }
//        开始运行
                state = AgentState.RUNNING;

                List<String> resultList = new ArrayList<String>();

                messageList.add(new UserMessage(userPrompt));

//        for循环
                try {
                    for (int i = 0; i < maxSteps && state != AgentState.FINISHED; i++) {
                        currentStep = i + 1;
                        log.info("Progress: {}/{}", currentStep, maxSteps);

                        String stepResult = step();
                        String result = "now step " + currentStep + " , " + stepResult;
                        resultList.add(result);
                        sendEmitter.send(result);

//                        TODO 这里需要优化一下
                        if ("思考完成,不需要执行".contains(stepResult)) {
                            state = AgentState.FINISHED;
                        }
                        if (currentStep >= maxSteps) {
                            state = AgentState.FINISHED;
                            resultList.add("Terminal Agent reached max steps" + maxSteps);
                            sendEmitter.send("执行结束,达到最大步骤(" + maxSteps + ")");

                        }
                    }
//                        正常完成
                    sendEmitter.complete();

                } catch (Exception e) {

                    try {
                        sendEmitter.send("执行错误: " + e.getMessage());
                        sendEmitter.complete();
                    } catch (IOException ex) {
                        sendEmitter.completeWithError(ex);
                    }
                } finally {
                    clearCache();
                }
        });
        sendEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.clearCache();
            log.warn("执行错误");
        });
        sendEmitter.onCompletion(() -> {
            this.state = (AgentState.FINISHED);
            this.clearCache();

        });

        return sendEmitter;

    }

    /**
     * 清理缓存
     */
    public void clearCache() {
    }

    /**
     * 下一步执行
     */
    public String step() {
        return null;
    }


}
