package iuh.fit.applicationservice.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class PdfTextExtractionService {

    private static final int MAX_PDF_BYTES = 8 * 1024 * 1024;
    private static final int MAX_EXTRACTED_TEXT_LENGTH = 12000;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public String extractText(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank() || !fileUrl.toLowerCase().contains(".pdf")) {
            return "";
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return "";
            }

            byte[] pdfBytes = response.body();
            if (pdfBytes == null || pdfBytes.length == 0 || pdfBytes.length > MAX_PDF_BYTES) {
                return "";
            }

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String text = stripper.getText(document);
                if (text == null || text.isBlank()) {
                    return "";
                }
                String normalized = text.replaceAll("\\s+", " ").trim();
                return normalized.length() > MAX_EXTRACTED_TEXT_LENGTH
                        ? normalized.substring(0, MAX_EXTRACTED_TEXT_LENGTH)
                        : normalized;
            }
        } catch (Exception ignored) {
            return "";
        }
    }
}
