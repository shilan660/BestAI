package com.best.agent.rag;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;


/**
 * 创建自定义的 RAG 检索增强顾问的工厂
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {


    /**
     * 创建自定义的 RAG 检索增强顾问
     * @param vectorStore 向量库
     * @param status 状态(单身or)
     * @return advisor
     */
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore , String status) {

        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status" , status) //过滤方法
                .build();

//        因为检索的是向量库
        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore) //向量库
                .filterExpression(expression) //过滤方法
                .similarityThreshold(0.6) //相似度阈值
                .topK(3) //前几条
                .build();

        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever) //文档检索器
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createLoveAppContextualQueryAugmenter())
                .build();
    }
}
