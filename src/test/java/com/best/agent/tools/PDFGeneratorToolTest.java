package com.best.agent.tools;

import org.junit.jupiter.api.Test;

class PDFGeneratorToolTest {

    @Test
    void generatePdf() {
        PDFGeneratorTool pdfGeneratorTool = new PDFGeneratorTool();
        String result = pdfGeneratorTool.generatePdf("yurou", "鱼");
    }
}