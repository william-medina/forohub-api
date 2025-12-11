package com.williammedina.forohub.infrastructure.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailContentBuilder {

    public String buildEmailContent(String title, String message, String buttonLabel, String url, String footer) {
        String template = loadTemplate("email_template.html")
                .replace("{TITLE}", title)
                .replace("{MESSAGE}", message)
                .replace("{FOOTER}", footer);

        // Check if buttonLabel and url are null
        String buttonSection = (buttonLabel != null && url != null)
                ? "<a href=\"" + url + "\" style=\"display: inline-block; background-color: #03dac5; color: #121212; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold; max-width: 100%; text-align: center;\">"
                + buttonLabel + "</a>"
                : "";

        return template.replace("{BUTTON_SECTION}", buttonSection);
    }

    // Loads a template file from the file system
    private String loadTemplate(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/" + fileName)) {
            if (inputStream == null) {
                log.error("Email template not found: {}", fileName);
                throw new RuntimeException("No se pudo encontrar la plantilla de email: " + fileName);
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            log.error("Error loading email template: {}. Details: {}", fileName, e.getMessage());
            throw new RuntimeException("Error al cargar la plantilla de email: " + fileName, e);
        }
    }
}
