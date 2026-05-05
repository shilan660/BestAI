package com.best.agent.tools;

import com.best.agent.constant.FileConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PDFGeneratorTool {
    private final String FILE_PATH = FileConstant.FILE_SAVE_DIR + "/file";
    /**
     * 根据内容生成PDF文件
     */
    @Tool(description = "Generate a PDF file from text content")
    public String generatePdf(
            @ToolParam(description = "File name (without .pdf)") String fileName,
            @ToolParam(description = "Content to write into PDF") String content
    ) {
        try {
            // 保存目录
            String dir = FILE_PATH + "/pdf";
            new File(dir).mkdirs();

            String filePath = dir + File.separator + fileName + ".pdf";

            // 创建 PDF
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // ⭐ 关键：中文字体（内置STSong）
            PdfFont font = PdfFontFactory.createFont(
                    "STSongStd-Light",
                    "UniGB-UCS2-H",
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED
            );

            // 写入内容
            document.add(new Paragraph(content).setFont(font));

            document.close();

            return "PDF生成成功：" + filePath;

        } catch (Exception e) {
            return "PDF生成失败：" + e.getMessage();
        }
    }
}
