//package com.best.agent.demo.invoke;
//
//import dev.langchain4j.community.model.dashscope.QwenChatModel;
//import dev.langchain4j.model.chat.ChatModel;
//
//public class LangChain4jAiInvoke {
//    public static void main(String[] args) {
//        ChatModel chatModel = QwenChatModel.builder()
//                .apiKey(TestApiKey.API_KEY)
//                .modelName("qwen-max")
//                .build();
//        String answer = chatModel.chat("你知道晋江作者千里江风吗");
//        System.out.println(answer);
//    }
//}
