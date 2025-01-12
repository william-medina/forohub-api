package com.williammedina.forohub.domain.common;

import com.williammedina.forohub.infrastructure.errors.AppException;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContentValidationService {

    private final ChatClient chatClient;

    @Value("${ai.enabled}")
    private boolean isAiEnabled;

    public ContentValidationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public boolean validateContent(String texto) {

        if (!isAiEnabled) return true;  // Si la IA está deshabilitada, solo devolvemos true sin hacer validaciones

        String systemMessage = """
                Eres una IA de moderación de contenido. Debes evaluar el texto y determinar si contiene contenido ofensivo, racista, xenófobo o inapropiado.
                Devuelve 'true' si el contenido es adecuado y 'false' si es inapropiado.
                Texto:
            """;

        try {
            // Llamar al modelo de OpenAI para obtener la evaluación
            String aiResponse = chatClient.prompt()
                    .system(systemMessage)
                    .user(texto)
                    .call()
                    .content();

            // Verificar si la respuesta es "true" o "false"
            if ("true".equalsIgnoreCase(aiResponse.trim())) {
                return true;
            } else if ("false".equalsIgnoreCase(aiResponse.trim())) {
                return false;
            } else {
                // Si la respuesta no es "true" ni "false", devolvemos false por defecto
                return false;
            }
        } catch (Exception e) {
            throw new AppException("Error al validar el contenido con la IA", "SERVICE_UNAVAILABLE");
        }
    }

    public boolean validateUsername(String username) {
        String systemMessage = """
                Eres una IA de moderación de contenido. Debes evaluar si el siguiente nombre de usuario es adecuado. 
                El nombre de usuario no debe contener palabras ofensivas, racistas, xenófobas o inapropiadas.
                Devuelve 'true' si el nombre de usuario es adecuado y 'false' si es inapropiado.
                Nombre de usuario:
            """;

        try {
            // Llamar al modelo de OpenAI para evaluar el nombre de usuario
            String aiResponse = chatClient.prompt()
                    .system(systemMessage)
                    .user(username)
                    .call()
                    .content();

            // Verificar si la respuesta es "true" o "false"
            if ("true".equalsIgnoreCase(aiResponse.trim())) {
                return true;
            } else if ("false".equalsIgnoreCase(aiResponse.trim())) {
                return false;
            } else {
                // Si la respuesta no es "true" ni "false", devolvemos false por defecto
                return false;
            }
        } catch (Exception e) {
            throw new AppException("Error al validar el nombre de usuario con la IA", "SERVICE_UNAVAILABLE");
        }
    }
}
