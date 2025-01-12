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

    public String validateContent(String texto) {

        if (!isAiEnabled) return "approved";  // Si la IA está deshabilitada, solo devolvemos "approved" sin hacer validaciones

        String systemMessage = """
                Eres una IA de moderación de contenido. Tu tarea es evaluar el texto proporcionado y determinar si contiene contenido inapropiado. Debes identificar cualquier forma de contenido que pueda ser considerado:
            
                Tipos de contenido inapropiado que debes buscar:
                - Ofensivo
                - Racista
                - Xenófobo
                - Inapropiado
                - Spam
                - Desinformación
                - Amenazas
                - Contenido explícito
                - Repetitivo
                - Sin sentido
                - Cualquier otra forma de contenido nocivo
            
                Si el contenido es inapropiado, responde con una breve descripción de los problemas encontrados, siempre iniciando con la palabra "contiene" en minúscula. No utilices caracteres especiales ni mayúsculas al principio. La respuesta debe finalizar con un punto.
            
                Ejemplos de respuestas inapropiadas:
                - "contiene palabras sin sentido"
                - "contiene palabras repetitivas"
                - "contiene caracteres ofensivos"
                - "contiene información errónea"
            
                Si el contenido es adecuado (sin problemas), responde con:
                - "approved" (sin puntos ni caracteres especiales)
            
                Ejemplo de respuesta válida:
                - "approved"
            
                Texto:
            """;

        try {
            // Llamar al modelo de OpenAI para obtener la evaluación
            String aiResponse = chatClient.prompt()
                    .system(systemMessage)
                    .user(texto)
                    .call()
                    .content();

            return aiResponse.trim();

        } catch (Exception e) {
            throw new AppException("Error al validar el contenido con la IA", "SERVICE_UNAVAILABLE");
        }
    }


    public String validateUsername(String username) {

        if (!isAiEnabled) return "approved";  // Si la IA está deshabilitada, solo devolvemos "approved" sin hacer validaciones

        String systemMessage = """
            Eres una IA de moderación de contenido. Tu tarea es evaluar el siguiente nombre de usuario y determinar si es adecuado. Debes verificar si el nombre contiene algún tipo de contenido inapropiado o no deseado.
        
            Tipos de problemas que debes buscar en el nombre de usuario:
                - Ofensivo
                - Racista
                - Xenófobo
                - Inapropiado
                - Aleatorio o sin sentido (como cadenas de caracteres sin un propósito claro)
                - Cualquier otra forma de contenido nocivo
        
            Si el nombre de usuario es inapropiado, responde con una breve descripción de los problemas encontrados, siempre iniciando con la palabra "contiene" en minúscula. No utilices caracteres especiales ni mayúsculas al principio. La respuesta debe finalizar con un punto.
        
            Ejemplos de respuestas inapropiadas:
            - "contiene palabras ofensivas"
            - "contiene nombre de usuario aleatorio"
            - "contiene caracteres inapropiados"
        
            Si el nombre de usuario es adecuado (sin problemas), responde con:
            - "approved" (sin puntos ni caracteres especiales)
        
            Ejemplo de respuesta válida:
            - "approved"
        
            Nombre de usuario:
        """;

        try {
            // Llamar al modelo de OpenAI para evaluar el nombre de usuario
            String aiResponse = chatClient.prompt()
                    .system(systemMessage)
                    .user(username)
                    .call()
                    .content();

            return aiResponse.trim();

        } catch (Exception e) {
            throw new AppException("Error al validar el nombre de usuario con la IA", "SERVICE_UNAVAILABLE");
        }
    }
}
