package com.williammedina.forohub.infrastructure.contentvalidation;

import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import com.williammedina.forohub.infrastructure.contentvalidation.prompts.AIContentPrompts;
import com.williammedina.forohub.infrastructure.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "ai.enabled", havingValue = "true")
public class AIContentValidationService implements ContentValidationService {

    private final ChatClient chatClient;

    public AIContentValidationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String validateContent(String content) {
        return callAI(AIContentPrompts.CONTENT_VALIDATION_PROMPT, content, "content");
    }

    @Override
    public String validateUsername(String username) {
        return callAI(AIContentPrompts.USERNAME_VALIDATION_PROMPT, username, "username");
    }

    private String callAI(String systemPrompt, String userInput, String type) {
        try {
            String aiResponse = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userInput)
                    .call()
                    .content();

            log.info("AI validation result for {}: {}", type, aiResponse.trim());
            return aiResponse.trim();
        } catch (Exception e) {
            log.error("Error validating {} with AI", type, e);
            throw new AppException("Error al validar " + type + " con IA", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

}
