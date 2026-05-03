package com.best.agent.rag;

import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 恋爱大师向量库配置类(初始化基于内存的向量数据库配置)
 */
@Configuration
public class LoveAppVectorStoreConfig {
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Bean
    SimpleVectorStore loveAppVectorStore(EmbeddingModel dashScopeEmbeddingModel) {
        SimpleVectorStore simpleStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
        List<Document> documents = loveAppDocumentLoader.loadDocuments();
        simpleStore.add(documents);
        return simpleStore;
    }
}
