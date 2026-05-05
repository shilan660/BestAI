package com.best.agent.tools;

import org.junit.jupiter.api.Test;

class ResourceDownLoadToolTest {

    @Test
    void downloadResource() {
        ResourceDownLoadTool resourceDownLoadTool = new ResourceDownLoadTool();
        resourceDownLoadTool.downloadResource("https://www.codefather.cn/logo.png");
    }
}