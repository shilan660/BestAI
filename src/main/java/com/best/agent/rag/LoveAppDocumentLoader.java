package com.best.agent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * AI大师应用文档加载器
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {
//    初始化resourcePatternResolver
    private final ResourcePatternResolver resourcePatternResolver;
    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载所有的markdown文档
     * @return 文档列表
     */
    public List<Document> loadDocuments() {
        List<Document> documents = new ArrayList<>();
        try {
//            读取文件
            Resource[] resources = resourcePatternResolver.getResources("classpath:/document/*.md");
            for (Resource resource : resources) {
                String fileName = resource.getFilename();

                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", fileName)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                documents.addAll(reader.get());

            }

        } catch (IOException e) {
            log.error("Markdown文档加载失败", e);
        }
        return documents;
    }
}
