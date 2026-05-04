package com.best.agent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PgVectorVectorStoreConfigTest {
    @Resource(name = "PgVectorStore")
    private VectorStore pgVectorVectorStore;
    @Resource
    private EmbeddingModel dashscopeEmbeddingModel;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("今天吃啥啊? 吃鱼", Map.of("meta1", "meta1")),
                new Document("李氏小饭馆"),
                new Document("他家的鱼香肉丝很好吃", Map.of("meta2", "meta2")));

        pgVectorVectorStore.add(documents);

        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        Assertions.assertNotNull(results);
    }
    
    @Test
    void test1(){
        EmbeddingResponse response = dashscopeEmbeddingModel.embedForResponse(List.of("测试"));
        System.out.println(response.getResults().get(0).getOutput().length);
    }
    
}