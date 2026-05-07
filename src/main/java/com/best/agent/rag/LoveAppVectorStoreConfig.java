package com.best.agent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 恋爱大师向量库配置类(初始化基于内存的向量数据库配置)(本地)
 */
@Configuration
public class LoveAppVectorStoreConfig {
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;
    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeyWordEnricher myKeyWordEnricher;

    @Bean
    SimpleVectorStore loveAppVectorStore(EmbeddingModel dashScopeEmbeddingModel) {
        SimpleVectorStore simpleStore = SimpleVectorStore.builder(dashScopeEmbeddingModel).build();
//        加载文档
        List<Document> documents = loveAppDocumentLoader.loadDocuments();

//        List<Document> documentsNew = myKeyWordEnricher.enrichDocuments(documents);

//        默认切分
//        List<Document> documentsNew = myTokenTextSplitter.splitDocuments(documents);

//        List<Document> documentsSelf = myTokenTextSplitter.splitCustomized(documents);
        simpleStore.add(documents);
        return simpleStore;
    }
}
