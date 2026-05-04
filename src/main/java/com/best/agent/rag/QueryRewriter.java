package com.best.agent.rag;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询重写
 */
@Component
public class QueryRewriter {

    private final QueryTransformer queryTransformer;

//    配置重写器
//    这里不能用字段注入
    public QueryRewriter(ChatModel dashScopeChatModel) {
        ChatClient.Builder clientBuilder = ChatClient.builder(dashScopeChatModel);

        queryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(clientBuilder)
                .build();
    }

//    实现重写方法
    public String doQueryRewriter(String prompt){
//      初始化问题
        Query query = new Query(prompt);
//        调用重写方法1
        Query newQuery = queryTransformer.transform(query);
//        返回新问题
        return  newQuery.text();
    }
}
