INSERT INTO topics (course_id, user_id, title, description, status, created_at, updated_at)
VALUES
-- Tópicos para el curso 'Introducción a Java y Programación Orientada a Objetos'
    (1, 1, 'Error en instalación de JDK', 'No puedo instalar el JDK correctamente en mi sistema, ¿cómo puedo solucionarlo?', 'CLOSED', DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY)),
    (1, 1, 'Problema con la herencia', 'No entiendo bien cómo funcionan las clases y la herencia en Java, ¿podrían darme un ejemplo práctico?', 'CLOSED', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL -2 DAY)),
    (1, 3, '¿Cuándo usar interfaces?', '¿En qué casos es mejor usar interfaces en lugar de herencia directa?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY)),

-- Tópicos para el curso 'Desarrollo de Aplicaciones Web con Spring Boot'
    (2, 1, 'Error al levantar servidor con Spring Boot', 'Intento iniciar el servidor pero me da un error de configuración, ¿qué puedo revisar?', 'CLOSED', DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL -3 DAY)),
    (2, 2, 'Problemas con controladores REST', 'No logro hacer que mi controlador responda a las peticiones, ¿qué puede estar fallando?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL -2 DAY)),
    (2, 1, 'Integración de base de datos con JPA', '¿Cuál es la mejor forma de conectar una base de datos MySQL con Spring Boot usando JPA?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY)),

-- Tópicos para el curso 'Spring Boot: Creación de API RESTful'
    (3, 1, 'Manejo de excepciones en API REST', '¿Cómo puedo personalizar las respuestas de error en mi API REST con Spring Boot?', 'CLOSED', DATE_ADD(NOW(), INTERVAL -6 DAY), DATE_ADD(NOW(), INTERVAL -2 DAY)),
    (3, 2, 'Autenticación y autorización en APIs', '¿Cuál es la mejor manera de implementar autenticación y autorización en una API REST con Spring Security?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY)),
    (3, 3, 'Problema con los métodos POST y GET', 'Los métodos POST y GET no devuelven los datos esperados, ¿qué puedo verificar?', 'CLOSED', DATE_ADD(NOW(), INTERVAL -8 DAY), DATE_ADD(NOW(), INTERVAL -2 DAY)),

-- Tópicos para el curso 'Desarrollo Frontend con React'
    (4, 1, 'Problema con props en TypeScript', 'Recibo un error al pasar props a un componente en TypeScript, ¿cómo puedo corregirlo?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -9 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY)),
    (4, 2, 'State Management en React', '¿Cuál es la mejor librería para manejar el estado en aplicaciones grandes en React?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL -3 DAY)),
    (4, 3, 'Error de sintaxis en JSX', 'Al compilar mi proyecto, aparece un error de sintaxis en mi código JSX, ¿qué podría estar fallando?', 'CLOSED', DATE_ADD(NOW(), INTERVAL -11 DAY), DATE_ADD(NOW(), INTERVAL -1 DAY)),

-- Tópicos para el curso 'React + TypeScript: Arquitectura y Optimización de Aplicaciones'
    (5, 1, 'Optimización de rendimiento en React', '¿Qué estrategias puedo usar para mejorar el rendimiento de mi aplicación en React?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -12 DAY), DATE_ADD(NOW(), INTERVAL -5 DAY)),
    (5, 2, 'Problema con Hooks personalizados', 'Al crear un hook personalizado, obtengo resultados inesperados, ¿qué debería verificar?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -13 DAY), DATE_ADD(NOW(), INTERVAL -7 DAY)),
    (5, 3, 'Organización de carpetas en un proyecto grande', '¿Cuál es una buena estructura de carpetas para un proyecto de React + TypeScript?', 'ACTIVE', DATE_ADD(NOW(), INTERVAL -14 DAY), DATE_ADD(NOW(), INTERVAL -4 DAY));
