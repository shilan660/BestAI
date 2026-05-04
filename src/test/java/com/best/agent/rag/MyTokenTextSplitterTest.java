package com.best.agent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MyTokenTextSplitterTest {

    @Resource
    LoveAppDocumentLoader loveAppDocumentLoader;


    @Test
    void splitDocuments() {
        List<Document> documents = loveAppDocumentLoader.loadDocuments();

            ;
    }

    @Test
    void splitCustomized() {
    }
}