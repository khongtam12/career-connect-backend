package iuh.fit.cvservice.service;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class PDFExportService {

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private Playwright playwright;
    private Browser browser;

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing Playwright...");
            playwright = Playwright.create();
            // Launching browser. It will download the browser if not present.
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true));
            log.info("Playwright initialized successfully.");
        } catch (Exception e) {
            log.error("Failed to initialize Playwright: ", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    public byte[] exportCVToPDF(String cvId, String token) {
        try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 1024))) {

            // Set the authentication cookie if provided
            if (token != null) {
                com.microsoft.playwright.options.Cookie cookie = new com.microsoft.playwright.options.Cookie("access_token", token);
                cookie.setDomain("localhost"); 
                cookie.setPath("/");
                context.addCookies(Collections.singletonList(cookie));
            }

            Page page = context.newPage();
            
            // Construct the URL
            String url = String.format("%s/cv-builder?id=%s&print=1", frontendUrl, cvId);
            log.info("Navigating to URL for PDF export: {}", url);

            // Navigate and wait for content to load
            page.navigate(url);
            page.waitForLoadState(LoadState.NETWORKIDLE);
            
            // Wait a bit more for any animations/fonts
            page.waitForTimeout(2000);

            // Export to PDF
            Page.PdfOptions pdfOptions = new Page.PdfOptions()
                    .setFormat("A4")
                    .setPrintBackground(true)
                    .setMargin(new com.microsoft.playwright.options.Margin()
                            .setTop("10mm").setBottom("10mm").setLeft("10mm").setRight("10mm"));

            return page.pdf(pdfOptions);
        } catch (Exception e) {
            log.error("Error exporting CV to PDF: ", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
