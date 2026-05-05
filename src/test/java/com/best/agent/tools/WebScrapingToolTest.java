package com.best.agent.tools;

import org.junit.jupiter.api.Test;

class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String html = webScrapingTool.scrapeWebPage("https://www.baidu.com");
        System.out.println(html);
    }
}