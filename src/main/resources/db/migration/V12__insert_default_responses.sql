INSERT INTO responses (topic_id, user_id, content, solution, created_at, updated_at)
VALUES
-- Respuestas para el topic 'Error en instalación de JDK'
    (1, 2, 'A mí también me pasó lo mismo. Revisé si la variable de entorno JAVA_HOME estaba bien configurada y funcionó.', FALSE, DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL -2 HOUR)),
    (1, 3, 'Verifica si estás descargando la versión correcta del JDK según tu sistema operativo. A veces, el instalador para 64 bits falla en 32 bits.', FALSE, DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL -1 HOUR)),
    (1, 4, 'La instalación de JDK suele fallar por problemas con los permisos. Intenta ejecutar el instalador como administrador. Esto debería solucionar el problema.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -2 HOUR)),

-- Respuestas para el topic 'Problema con la herencia'
    (2, 3, 'La herencia en Java puede ser confusa al principio. ¿Has probado con clases abstractas? A veces ayudan a hacer la estructura más clara.', FALSE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
    (2, 4, 'Un ejemplo básico sería tener una clase `Animal` con un método `hacerSonido()` y luego crear clases como `Perro` que hereden de `Animal` y sobreescriban el método para hacerlo específico para cada animal.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -3 HOUR)),

-- Respuestas para el topic '¿Cuándo usar interfaces?'
    (3, 1, 'Yo también me hice esa pregunta. Generalmente, las interfaces son útiles cuando quieres que varias clases tengan un conjunto común de métodos, pero sin necesidad de compartir implementación.', FALSE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -5 HOUR)),

-- Respuestas para el topic 'Error al levantar servidor con Spring Boot'
    (4, 3, 'Asegúrate de que tu archivo `application.properties` esté configurado correctamente y que no haya conflictos en los puertos.', FALSE, DATE_ADD(NOW(), INTERVAL -2 DAY), DATE_ADD(NOW(), INTERVAL -10 MINUTE)),
    (4, 4, 'Revisa si tienes todas las dependencias necesarias en tu archivo `pom.xml`. A veces, el servidor no inicia por falta de configuraciones específicas.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -1 HOUR)),

-- Respuestas para el topic 'Problemas con controladores REST'
    (5, 1, 'Verifica si el controlador está anotado correctamente con `@RestController` y si los métodos están correctamente mapeados con `@RequestMapping` o las anotaciones correspondientes.', FALSE, DATE_ADD(NOW(), INTERVAL -3 DAY), DATE_ADD(NOW(), INTERVAL -45 MINUTE)),
    (5, 3, 'Asegúrate de que el tipo de retorno de tu método es correcto. A veces los errores de mapeo ocurren si el retorno no es un tipo adecuado para ser convertido a JSON.', FALSE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -1 HOUR)),

-- Respuestas para el topic 'Integración de base de datos con JPA'
    (6, 2, 'A mí me funcionó agregar la configuración `spring.datasource.url` en el archivo `application.properties` para asegurarme de que está conectado correctamente.', FALSE, DATE_ADD(NOW(), INTERVAL -4 DAY), DATE_ADD(NOW(), INTERVAL -30 MINUTE)),

-- Respuestas para el topic 'Manejo de excepciones en API REST'
    (7, 2, 'Puedes utilizar `@ControllerAdvice` para manejar excepciones globalmente en tu API REST. Es una forma más limpia de gestionar los errores.', FALSE, DATE_ADD(NOW(), INTERVAL -5 DAY), DATE_ADD(NOW(), INTERVAL -1 HOUR)),
    (7, 4, 'Utiliza `@ExceptionHandler` para capturar excepciones específicas y devolver una respuesta personalizada con el código de estado adecuado. Esto mejorará la experiencia del usuario.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -2 HOUR)),

-- Respuestas para el topic 'Autenticación y autorización en APIs'
    (8, 1, 'He utilizado JWT para manejar la autenticación. Configura un filtro de seguridad para verificar los tokens antes de cada solicitud.', FALSE, DATE_ADD(NOW(), INTERVAL -6 DAY), DATE_ADD(NOW(), INTERVAL -4 HOUR)),

-- Respuestas para el topic 'Problema con los métodos POST y GET'
    (9, 2, 'Asegúrate de que estás enviando los datos en el formato correcto. Si es JSON, asegúrate de que los encabezados de la solicitud estén correctamente configurados como `Content-Type: application/json`.', FALSE, DATE_ADD(NOW(), INTERVAL -7 DAY), DATE_ADD(NOW(), INTERVAL -2 HOUR)),
    (9, 4, 'Revisa el código de estado de las respuestas. Si no es 200 OK, significa que algo no está configurado correctamente. A veces el método no se ejecuta correctamente si no se manejan los errores de forma adecuada.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -5 MINUTE)),

-- Respuestas para el topic 'Problema con props en TypeScript'
--   (10, 3, 'Verifica si el tipo de datos que pasas como props es el correcto. Si el tipo no coincide con lo esperado, TypeScript generará un error.', FALSE, DATE_ADD(NOW(), INTERVAL -8 DAY), NOW()),

-- Respuestas para el topic 'State Management en React'
    (11, 1, 'Puedes usar Redux para manejar el estado en aplicaciones grandes, pero también hay otras opciones como Recoil, Context API.', FALSE, DATE_ADD(NOW(), INTERVAL -9 DAY), DATE_ADD(NOW(), INTERVAL -3 HOUR)),

-- Respuestas para el topic 'Error de sintaxis en JSX'
    (12, 2, 'Asegúrate de que todas las etiquetas JSX estén cerradas correctamente. A veces un error en JSX se debe a una etiqueta abierta sin su cierre correspondiente.', FALSE, DATE_ADD(NOW(), INTERVAL -10 DAY), DATE_ADD(NOW(), INTERVAL -15 MINUTE)),
    (12, 4, 'El error en JSX generalmente se debe a un mal uso de las llaves `{}` o a la falta de una clave `key` en un elemento dentro de una lista. Revisa ambos casos.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -10 MINUTE)),

-- Respuestas para el topic 'Optimización de rendimiento en React'
--    (13, 3, 'He utilizado la técnica de memoización para evitar renderizados innecesarios, lo cual mejora bastante el rendimiento.', FALSE, DATE_ADD(NOW(), INTERVAL -11 DAY), NOW()),

-- Respuestas para el topic 'Problema con Hooks personalizados'
    (14, 3, 'Asegúrate de que el hook que estás creando esté correctamente optimizado y no esté generando renders innecesarios. Usa `useMemo` o `useCallback` si es necesario.', FALSE, DATE_ADD(NOW(), INTERVAL -12 DAY), DATE_ADD(NOW(), INTERVAL -3 HOUR)),

-- Respuestas para el topic 'Organización de carpetas en un proyecto grande'
    (15, 1, 'Es importante mantener una estructura modular. Te recomiendo dividir las carpetas por características, como `components`, `hooks`, `services`, etc.', FALSE, DATE_ADD(NOW(), INTERVAL -13 DAY), DATE_ADD(NOW(), INTERVAL -1 HOUR)),
    (15, 2, 'También puedes usar el patrón de "feature-first" donde agruparás todo lo relacionado a una funcionalidad en una sola carpeta, para facilitar su mantenimiento.', TRUE, DATE_ADD(NOW(), INTERVAL -1 DAY), DATE_ADD(NOW(), INTERVAL -2 HOUR));
