package com.williammedina.forohub.infrastructure.contentvalidation;

import com.williammedina.forohub.domain.contentvalidation.ContentValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(value = "ai.enabled", havingValue = "false", matchIfMissing = true)
public class DisabledContentValidationService implements ContentValidationService {

    @Override
    public String validateContent(String content) {
        log.info("[DISABLED AI] Content validation skipped. Returning 'approved'. Content: {}", content);
        return "approved";
    }

    @Override
    public String validateUsername(String username) {
        log.info("[DISABLED AI] Username validation skipped. Returning 'approved'. Username: {}", username);
        return "approved";
    }

}
