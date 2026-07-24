package com.williammedina.forohub.infrastructure.email.resend;

import com.williammedina.forohub.infrastructure.email.EmailContentBuilder;
import com.williammedina.forohub.infrastructure.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "email.provider", havingValue = "resend")
public class ResendEmailSender implements EmailSender {

    private final WebClient webClient;
    private final EmailContentBuilder emailContentBuilder;

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${email.from}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String title, String message, String buttonLabel, String url, String footer) {

        String html = emailContentBuilder.buildEmailContent(title, message, buttonLabel, url, footer);

        ResendEmailRequest request = new ResendEmailRequest(fromEmail, List.of(to), subject, html);

        String response = webClient.post()
                .uri("https://api.resend.com/emails")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    log.error("Resend error {}: {}", clientResponse.statusCode(), body);
                                    return Mono.error(new RuntimeException(body));
                                })
                )
                .bodyToMono(String.class)
                .block();

        log.info("Resend response: {}", response);
    }
}
