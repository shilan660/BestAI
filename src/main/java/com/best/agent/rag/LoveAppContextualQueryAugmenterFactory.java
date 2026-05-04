package com.best.agent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.stereotype.Component;

/**
 * 创建上下文增强器的工厂
 */
@Component
public class LoveAppContextualQueryAugmenterFactory {
    public static ContextualQueryAugmenter createLoveAppContextualQueryAugmenter() {
        PromptTemplate emptyTemplate = new PromptTemplate(
                """
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系客服
                """
        );
        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyTemplate)
                .build();
    }
}
