package com.examconnect.ai;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PdfExtractorService {

    public record ExtractedPage(int pageNumber, String text) {}

    public record DocumentChunk(int chunkIndex, int pageNumber, String content) {}

    public List<ExtractedPage> extractPages(File pdfFile) throws IOException {
        List<ExtractedPage> pages = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int totalPages = document.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();

            for (int p = 1; p <= totalPages; p++) {
                stripper.setStartPage(p);
                stripper.setEndPage(p);
                String pageText = stripper.getText(document);
                if (pageText != null && !pageText.trim().isEmpty()) {
                    pages.add(new ExtractedPage(p, pageText.trim()));
                }
            }
        }

        return pages;
    }

    public List<DocumentChunk> chunkText(List<ExtractedPage> pages, int chunkSize, int chunkOverlap) {
        List<DocumentChunk> chunks = new ArrayList<>();
        int chunkIndex = 0;

        for (ExtractedPage page : pages) {
            String text = page.text().replaceAll("\\s+", " ").trim();
            if (text.length() <= chunkSize) {
                chunkIndex++;
                chunks.add(new DocumentChunk(chunkIndex, page.pageNumber(), text));
                continue;
            }

            int start = 0;
            while (start < text.length()) {
                int end = Math.min(start + chunkSize, text.length());
                // Try not to break in the middle of a sentence
                if (end < text.length()) {
                    int lastPeriod = text.lastIndexOf(". ", end);
                    if (lastPeriod > start + (chunkSize / 2)) {
                        end = lastPeriod + 1;
                    }
                }

                String slice = text.substring(start, end).trim();
                if (!slice.isEmpty()) {
                    chunkIndex++;
                    chunks.add(new DocumentChunk(chunkIndex, page.pageNumber(), slice));
                }

                if (end >= text.length()) {
                    break;
                }
                start = Math.max(end - chunkOverlap, start + 1);
            }
        }

        return chunks;
    }
}
