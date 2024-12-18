# ForoHub API
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Framework-Spring%20Boot-brightgreen)
![MySQL](https://img.shields.io/badge/Database-MySQL-orange)
![Hibernate](https://img.shields.io/badge/ORM-Hibernate-orange)
![REST API](https://img.shields.io/badge/API-REST-green)
![API Documentation](https://img.shields.io/badge/API%20Docs-Swagger-green)
![JWT](https://img.shields.io/badge/Authentication-JWT-blue)
![Version](https://img.shields.io/badge/Version-1.0.0-brightgreen)
![License](https://img.shields.io/badge/License-MIT-yellow)
![Testing](https://img.shields.io/badge/Testing-JUnit-orange)

# Índice
1. [Descripción](#descripción)
2. [Características](#características)
3. [Tecnologías](#tecnologías)
4. [Dependencias](#dependencias)
5. [Requisitos](#requisitos)
6. [Instalación](#instalación)
7. [Guía de Uso](#guía-de-uso)
    - [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
    - [Acceso a la API REST](#acceso-a-la-api-rest)
    - [Documentación de la API](#documentación-de-la-api)
8. [Endpoints](#endpoints)
    - [Endpoints de Autenticación](#endpoints-de-autenticación)
    - [Endpoints de Tópicos](#endpoints-de-tópicos)
    - [Endpoints de Respuestas](#endpoints-de-respuestas)
    - [Endpoints de Notificaciones](#endpoints-de-notificaciones)
9. [Testing](#testing)
10. [Frontend](#frontend)
11. [Licencia](#licencia)
12. [Autor](#autor)

## Descripción
**ForoHub** es una API desarrollada con **Spring Boot** que permite la creación, administración y gestión de tópicos y respuestas para una plataforma de discusión basada en cursos. Los usuarios pueden registrarse, interactuar con los tópicos, responder a ellos, y gestionar sus perfiles. Los administradores, moderadores e instructores tienen permisos especiales para gestionar contenidos y marcar soluciones en los tópicos. Además está diseñada para ser utilizada junto a un frontend en React.

## Características

- **Registro y autenticación de usuarios**: Los usuarios pueden registrarse, confirmar su cuenta mediante un token de confirmación y recuperar su password mediante un token de olvido.
- **Gestión de tópicos**: Los usuarios pueden crear, editar y eliminar sus propios tópicos.
- **Respuestas a tópicos**: Los usuarios pueden responder a los tópicos y editar o eliminar sus respuestas.
- **Cambio de perfil**: Los usuarios autenticados pueden modificar su nombre de usuario y password.
- **Seguimiento de tópicos**: Los usuarios pueden seguir tópicos para recibir notificaciones sobre nuevas respuestas o cambios en el estado.
- **Notificaciones**: Los usuarios reciben notificaciones cuando se generan nuevas respuestas en un tópico que han creado o seguido, o cuando un tópico es marcado como solucionado.
- **Paginación y filtrado**: Los tópicos pueden ser filtrados por estado, palabras clave o curso, y la API soporta paginación para una mejor gestión de los contenidos.


## Tecnologías
- **Spring Boot**: Framework que facilita el desarrollo ágil de aplicaciones en Java, permitiendo una configuración mínima.
- **MySQL**: Sistema de gestión de bases de datos relacional utilizado para almacenar la información de la API.
- **JWT (JSON Web Token)**: Tecnología para la autenticación y autorización de usuarios mediante tokens seguros.
- **API REST**: Protocolo de comunicación que permite realizar operaciones de consulta sobre los datos almacenados.
- **Springdoc OpenAPI**: Biblioteca para la generación automática de documentación de la API en aplicaciones Spring Boot, que utiliza **Swagger UI** para visualizar y probar los endpoints de manera interactiva.


## Dependencias

Para que la API funcione correctamente, asegúrate de incluir las siguientes dependencias:

- **Spring Data JPA**: Facilita el acceso a bases de datos mediante la integración de JPA (Java Persistence API) para el manejo de datos en una base de datos relacional.
- **Spring Security**: Framework para la protección de aplicaciones mediante autenticación y autorización, gestionando la seguridad de la API.
- **Spring Boot Starter Validation**: Proporciona herramientas para validar objetos, parámetros de entrada y controlar los errores en la API.
- **Spring Boot Starter Web**: Proporciona las dependencias necesarias para construir una aplicación web, incluyendo controladores RESTful y manejo de solicitudes HTTP.
- **Flyway**: Herramienta para gestionar migraciones de bases de datos, que asegura que la estructura de la base de datos se mantenga consistente a lo largo del tiempo.
- **MySQL Connector**: Controlador JDBC para interactuar con bases de datos MySQL desde Java.
- **Lombok**: Biblioteca que facilita la creación de código repetitivo como getters, setters y constructores, usando anotaciones en el código.
- **Spring Boot DevTools**: Herramienta para mejorar la experiencia de desarrollo mediante recarga automática, depuración mejorada y más.
- **Java JWT (java-jwt)**: Biblioteca que permite trabajar con tokens JWT (JSON Web Tokens) para la autenticación y autorización de usuarios.
- **SpringDoc OpenAPI Starter**: Integra la especificación OpenAPI para documentar automáticamente los endpoints de la API.
- **Spring Boot Starter Mail**: Proporciona las herramientas necesarias para enviar correos electrónicos desde la aplicación, útil para el envío de correos de confirmación y recuperación de contraseñas.

Asegúrate de agregar las dependencias en el archivo `pom.xml` de tu proyecto Maven.

## Requisitos

- JDK 21 o superior
- Maven para la gestión de dependencias
- MySQL o cualquier base de datos relacional configurada
- IntelliJ IDEA (o cualquier otro IDE compatible con Java)
- Un servidor de email para el envio de notificaciones

## Instalación

1. **Clona el repositorio**:
    ```bash
    git clone https://github.com/william-medina/forohub-api.git
    ```

2. **Accede al directorio del proyecto**:
    ```bash
    cd forohub-api
    ```

3. **Agrega las variables de entorno**:  
   A continuación, agrega las siguientes variables de entorno directamente en la configuración de tu sistema operativo o IDE.

   ```dotenv
   # Base de Datos
   DB_URL=jdbc:mysql://localhost:3306/your_database_name
   DB_USERNAME=your_username
   DB_PASSWORD=your_password

   # Clave Secreta para JWT
   JWT_SECRET=your_secret_jwt

   # Configuración del Servidor de Email
   EMAIL_HOST=your_email_host
   EMAIL_PORT=your_email_port
   EMAIL_USER=your_email_user
   EMAIL_PASS=your_email_password
   EMAIL_FROM=your_email_from

   # URL del Frontend - Habilita CORS para permitir peticiones desde esta URL
   FRONTEND_URL=http://localhost:5173
   ```
   Reemplaza los valores de ejemplo con los detalles de tu configuración real.


4. **Habilitar o deshabilitar el envío de email:**

   Si no tienes un servidor de email o las credenciales correspondientes, puedes deshabilitar el envío de emails. Esto evitará que el sistema envíe notificaciones por email, incluyendo los tokens de la confirmación de cuenta o el restablecimiento del password. Para hacerlo, sigue estos pasos:

   - Abre el archivo `application.properties`.
   - Añade o modifica la siguiente línea:

     ```properties
     email.enabled=false
     ```

   Con esto, el envío de emails estará deshabilitado. Si en el futuro deseas habilitarlo, cambia el valor a `true`.


5. **Asegúrate de que todas las dependencias estén instaladas** utilizando la opción de **"Actualizar Proyecto"** o **"Importar dependencias"** en tu IDE.



## Guía de Uso

### Ejecución de la Aplicación
- Para iniciar la aplicación en modo API, ejecuta la clase `ForohubApplication` desde tu IDE o en la línea de comandos. Esta clase contiene el método `main`, que inicia la aplicación.

### Acceso a la API REST
- Una vez que la aplicación esté en ejecución, podrás acceder a la API REST a través de la URL base:
  ```
  http://localhost:8080/api
  ```
- Asegúrate de que el puerto configurado en tu aplicación sea el correcto (por defecto, es 8080).

### Documentación de la API
- La documentación de la API está disponible a través de [Swagger UI](http://localhost:8080/api/docs/swagger-ui/index.html) una vez que la aplicación esté en funcionamiento. Esta herramienta te permite explorar todos los endpoints disponibles y realizar pruebas directamente desde tu navegador, facilitando la interacción con la API.
   ```
   http://localhost:8080/api/docs/swagger-ui/index.html
   ```
  
## Endpoints
A continuación, se presenta la lista completa de endpoints disponibles en la API. Todos los endpoints comienzan con el prefijo `/api`.

### Endpoints de Autenticación
Estos endpoints permiten gestionar las cuentas de usuario, desde la creación hasta la actualización de password y nombres de usuario, así como la obtención de detalles y estadísticas del usuario autenticado.

| Endpoint                            | Método      | Descripción                                                                      |
|-------------------------------------|-------------|----------------------------------------------------------------------------------|
| `/auth/create-account`              | `POST`      | Crea una cuenta de usuario en el sistema.                                        |
| `/auth/confirm-account/{token}`     | `GET`       | Confirma la cuenta de usuario utilizando un token proporcionado.                 |
| `/auth/login`                       | `POST`      | Inicia sesión, autenticando al usuario y generando un token JWT.                 |
| `/auth/request-code`                | `POST`      | Solicita un nuevo código de confirmación y lo envía al email del usuario.        |
| `/auth/forgot-password`             | `POST`      | Genera un token de restablecimiento de password y lo envía al email del usuario. |
| `/auth/update-password/{token}`     | `POST`      | Permite actualizar el password utilizando un token de restablecimiento.          |
| `/auth/update-password`             | `PATCH`     | Permite al usuario autenticado actualizar su password actual.                    |
| `/auth/update-username`             | `PATCH`     | Permite al usuario autenticado actualizar su nombre de usuario.                  |
| `/auth/stats`                       | `GET`       | Obtiene estadísticas del usuario autenticado.                                    |
| `/auth/me`                          | `GET`       | Obtiene los detalles del usuario actualmente autenticado.                        |
---

### Endpoints de Tópicos
Estos endpoints gestionan la creación, obtención, actualización y eliminación de tópicos, así como el seguimiento de los mismos por parte de los usuarios. La eliminación de un tópico es lógica, es decir, no se elimina físicamente de la base de datos, sino que se marca como eliminado.

| Endpoint                                      | Método      | Descripción                                                                                             |
|-----------------------------------------------|-------------|---------------------------------------------------------------------------------------------------------|
| `/topic`                                      | `POST`      | Crea un nuevo tópico con los datos proporcionados.                                                      |
| `/topic`                                      | `GET`       | Obtiene todos los tópicos con paginación y filtrado opcional por curso, palabra clave y estado.         |
| `/topic/user/topics`                          | `GET`       | Obtiene los tópicos creados por el usuario con paginación y filtrado opcional por palabra clave.        |
| `/topic/{topicId}`                            | `GET`       | Obtiene un tópico específico por su ID, incluyendo todas sus respuestas.                                |
| `/topic/{topicId}`                            | `PUT`       | Actualiza los detalles de un tópico existente.                                                          |
| `/topic/{topicId}`                            | `DELETE`    | Elimina un tópico existente de manera lógica, marcándolo como eliminado (no se elimina físicamente).                                                                |
| `/topic/follow/{topicId}`                     | `POST`      | Permite a un usuario seguir o dejar de seguir un tópico específico.                                     |
| `/topic/user/followed-topics`                 | `GET`       | Obtiene los tópicos seguidos por el usuario con paginación y filtrado opcional por palabra clave.       |
---

### Endpoints de Respuestas
Los endpoints de respuestas gestionan la creación, actualización, eliminación y la posibilidad de marcar una respuesta como solución. Al igual que los tópicos, la eliminación de respuestas es lógica.

| Endpoint                                 | Método      | Descripción                                                                                                                                                                   |
|------------------------------------------|-------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/response`                              | `POST`      | Crea una nueva respuesta para un tópico específico.                                                                                                                           |
| `/response/user/responses`               | `GET`       | Obtiene todas las respuestas del usuario autenticado con paginación.                                                                                                          |
| `/response/{responseId}`                 | `GET`       | Obtiene una respuesta específica utilizando su ID.                                                                                                                            |
| `/response/{responseId}`                 | `PUT`       | Actualiza una respuesta específica.                                                                                                                                           |
| `/response/{responseId}`                 | `PATCH`     | Alterna el estado de una respuesta como solución o la quita si ya estaba marcada como solución. Además, actualiza el estado del tópico, indicándole si está activo o cerrado. |
| `/response/{responseId}`                 | `DELETE`    | Elimina una respuesta existente de manera lógica, marcándola como eliminada (no se elimina físicamente).                                                                      |
---

### Endpoints de Notificaciones
Estos endpoints gestionan las notificaciones del usuario, permitiendo obtenerlas, eliminarlas o marcarlas como leídas.

| Endpoint                             | Método       | Descripción                                                                                                      |
|--------------------------------------|--------------|------------------------------------------------------------------------------------------------------------------|
| `/notify`                            | `GET`        | Obtiene todas las notificaciones del usuario autenticado, ordenadas por fecha de creación.                       |
| `/notify/{notifyId}`                 | `DELETE`     | Elimina una notificación específica por su ID, si pertenece al usuario autenticado.                             |
| `/notify/{notifyId}`                 | `PATCH`      | Marca como leída una notificación específica por su ID, si pertenece al usuario autenticado.                    |
---

### Endpoint de Cursos
Este endpoint permite obtener información sobre los cursos disponibles en la API.

| Endpoint            | Método   | Descripción                                                                                 |
|---------------------|----------|---------------------------------------------------------------------------------------------|
| `/course`           | `GET`    | Recupera todos los cursos disponibles en la API, ordenados alfabéticamente por su nombre.  |


## Testing

La API cuenta con pruebas unitarias para cada repositorio y controlador. Las pruebas están diseñadas para garantizar que la funcionalidad de la API funcione correctamente. Cada prueba interactúa con una base de datos de pruebas, lo que permite realizar validaciones sin afectar los datos reales.

### Configuración de la base de datos para las pruebas

Antes de ejecutar las pruebas, es necesario configurar una base de datos separada que se utilizará exclusivamente para las pruebas. Esto garantiza que las pruebas no interfieran con los datos de producción. Para configurar la base de datos de pruebas, debes agrega las siguientes variables de entorno directamente en la configuración de tu sistema operativo o IDE.:

```dotenv
# Configuración de la base de datos para test
DB_URL_TEST=jdbc:mysql://localhost:3306/your_database_name_test?createDatabaseIfNotExist=true
DB_USERNAME_TEST=your_username
DB_PASSWORD_TEST=your_password
```
> **Importante**: El nombre de la base de datos debe ser diferente al de la base de datos principal. Esto es crucial porque cada prueba limpia los registros de las tablas al iniciar, garantizando que los tests sean independientes y no afecten los datos de producción.

- Al ejecutar las pruebas, el envío de email se deshabilitará automáticamente. Esto se hace para evitar que se envíen emails durante las pruebas, ya que no se requiere este comportamiento en este entorno.


## Frontend
La API cuenta con un frontend desarrollado en **React** utilizando **TypeScript** y **Tailwind CSS**. Este frontend está diseñado para interactuar de manera efectiva con la API.

### Acceso al Repositorio
El código fuente está disponible en el siguiente enlace:

👉 [Repositorio del Frontend en GitHub](https://github.com/william-medina/forohub-app)

### Ver el Proyecto en Producción
Puedes ver la aplicación en producción, ya conectada con la API, en el siguiente enlace:

👉 [Ver Proyecto en Producción](https://forohub.william-medina.com)

**Nota**: Si la API ha estado inactiva por un tiempo, puede demorar algunos momentos en iniciarse.

### Características Destacadas del Frontend
- **Interfaz Responsiva**: Diseñada para ofrecer una experiencia de usuario fluida en dispositivos móviles y de escritorio.
- **Conexión Eficiente a la API**: Permite la interacción en tiempo real con la API REST, facilitando búsquedas y visualización de datos de manera ágil.
- **Componentes Reutilizables**: Estructura modular que simplifica el mantenimiento y la escalabilidad de la aplicación.

### Imágenes del Frontend


A continuación, se presentan algunas capturas de pantalla del frontend de algunas paginas:

#### Página de Inicio:
<img src="./src/main/resources/static/images/frontend-home.png" alt="Frontend Home Page" width="600" style="display: block;" />

#### Detalles de los Tópicos:
<img src="./src/main/resources/static/images/frontend-topic.png" alt="Frontend Topic Page" width="600" style="display: block;" />

#### Página de Perfil:
<img src="./src/main/resources/static/images/frontend-profile.png" alt="Frontend Profile Page" width="600" style="display: block;" />

#### Notificaciones:
<img src="./src/main/resources/static/images/frontend-notify.png" alt="Frontend Notify Page" width="600" style="display: block;" />

## Licencia

Este proyecto está bajo la Licencia MIT. Para más detalles, consulta el archivo [LICENSE](./LICENSE).

## Autor

**William Medina**  
Autor y desarrollador de **ForoHub API**. Puedes encontrarme en [GitHub](https://github.com/william-medina)