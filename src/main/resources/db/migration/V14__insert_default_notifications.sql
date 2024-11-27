INSERT INTO notifications (user_id, topic_id, response_id, type, subtype, title, message, is_read, created_at) VALUES
    (1, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta a tu tópico', 'Tu tópico ha recibido una nueva respuesta. Ana García respondió al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos', 1, DATE_ADD(NOW(), INTERVAL -22 HOUR)),
    (3, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta en un tópico que sigues', 'Se ha añadido una nueva respuesta al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues.', 0, DATE_ADD(NOW(), INTERVAL -22 HOUR)),
    (1, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta a tu tópico', 'Tu tópico ha recibido una nueva respuesta. Alejandro Cristiano respondió al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos', 1, DATE_ADD(NOW(), INTERVAL -18 HOUR)),
    (2, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta en un tópico que sigues', 'Se ha añadido una nueva respuesta al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues.', 0, DATE_ADD(NOW(), INTERVAL -18 HOUR)),
    (1, 1, NULL, 'TOPIC', 'EDITED', 'Tu tópico ha sido editado', 'Se ha realizado cambios en tu tópico titulado \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos. Puedes revisar los detalles haciendo clic en el siguiente botón.', 1, DATE_ADD(NOW(), INTERVAL -10 HOUR)),
    (1, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta a tu tópico', 'Tu tópico ha recibido una nueva respuesta. Miguel Ángel respondió al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos', 1, DATE_ADD(NOW(), INTERVAL -9 HOUR)),
    (3, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta en un tópico que sigues', 'Se ha añadido una nueva respuesta al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues.', 0, DATE_ADD(NOW(), INTERVAL -9 HOUR)),
    (2, 1, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta en un tópico que sigues', 'Se ha añadido una nueva respuesta al tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues.', 0, DATE_ADD(NOW(), INTERVAL -9 HOUR)),
    (1, 1, NULL, 'TOPIC', 'SOLVED', 'Tu tópico ha sido marcado como solucionado', 'Tu tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos ha sido marcado como solucionado.', 1, DATE_ADD(NOW(), INTERVAL -5 HOUR)),
    (4, 1, 3, 'RESPONSE', 'SOLVED', 'Tu respuesta ha sido marcada como solución', 'Tu respuesta en el tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos ha sido marcada como solución.', 0, DATE_ADD(NOW(), INTERVAL -5 HOUR)),
    (3, 1, NULL, 'TOPIC', 'SOLVED', 'Un tópico que sigues ha sido marcado como solucionado', 'El tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues ha sido marcado como solucionado.', 0, DATE_ADD(NOW(), INTERVAL -5 HOUR)),
    (2, 1, NULL, 'TOPIC', 'SOLVED', 'Un tópico que sigues ha sido marcado como solucionado', 'El tópico \'Error en instalación de JDK\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues ha sido marcado como solucionado.', 0, DATE_ADD(NOW(), INTERVAL -5 HOUR)),
    (1, 2, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta a tu tópico', 'Tu tópico ha recibido una nueva respuesta. Alejandro Cristiano respondió al tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos', 1, DATE_ADD(NOW(), INTERVAL -3 HOUR)),
    (1, 2, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta a tu tópico', 'Tu tópico ha recibido una nueva respuesta. Miguel Ángel respondió al tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos', 1, DATE_ADD(NOW(), INTERVAL -2 HOUR)),
    (3, 2, NULL, 'TOPIC', 'REPLY', 'Nueva respuesta en un tópico que sigues', 'Se ha añadido una nueva respuesta al tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues.', 0, DATE_ADD(NOW(), INTERVAL -2 HOUR)),
    (3, 2, 4, 'RESPONSE', 'EDITED', 'Tu respuesta ha sido editada', 'Se han realizado cambios en tu respuesta del tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos. Puedes revisar los detalles haciendo clic en el siguiente botón.', 0, DATE_ADD(NOW(), INTERVAL -50 MINUTE)),
    (1, 2, NULL, 'TOPIC', 'SOLVED', 'Tu tópico ha sido marcado como solucionado', 'Tu tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos ha sido marcado como solucionado.', 0, DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
    (4, 2, 5, 'RESPONSE', 'SOLVED', 'Tu respuesta ha sido marcada como solución', 'Tu respuesta en el tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos ha sido marcada como solución.', 0, DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
    (3, 2, NULL, 'TOPIC', 'SOLVED', 'Un tópico que sigues ha sido marcado como solucionado', 'El tópico \'Problema con la herencia\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues ha sido marcado como solucionado.', 0, DATE_ADD(NOW(), INTERVAL -30 MINUTE)),
    (3, 3, NULL, 'TOPIC', 'SOLVED', 'Tu tópico ha sido marcado como solucionado', 'Tu tópico \'¿Cuándo usar interfaces?\' del curso: Introducción a Java y Programación Orientada a Objetos ha sido marcado como solucionado.', 0, DATE_ADD(NOW(), INTERVAL -2 MINUTE)),
    (1, 3, 6, 'RESPONSE', 'SOLVED', 'Tu respuesta ha sido marcada como solución', 'Tu respuesta en el tópico \'¿Cuándo usar interfaces?\' del curso: Introducción a Java y Programación Orientada a Objetos ha sido marcada como solución.', 0, DATE_ADD(NOW(), INTERVAL -2 MINUTE)),
    (1, 3, NULL, 'TOPIC', 'SOLVED', 'Un tópico que sigues ha sido marcado como solucionado', 'El tópico \'¿Cuándo usar interfaces?\' del curso: Introducción a Java y Programación Orientada a Objetos que sigues ha sido marcado como solucionado.', 0, DATE_ADD(NOW(), INTERVAL -2 MINUTE)),
    (1, 5, 9, 'RESPONSE', 'EDITED', 'Tu respuesta ha sido editada', 'Se han realizado cambios en tu respuesta del tópico \'Problemas con controladores REST\' del curso: Desarrollo de Aplicaciones Web con Spring Boot. Puedes revisar los detalles haciendo clic en el siguiente botón.', 0, DATE_ADD(NOW(), INTERVAL -2 MINUTE));