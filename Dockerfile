# Usar una imagen base con OpenJDK 21
FROM openjdk:21-jdk-slim AS build

# Instalar Maven manualmente
RUN apt-get update && apt-get install -y maven

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml .

RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src /app/src

# Construir el proyecto y empaquetar el JAR
RUN mvn clean package -DskipTests

# Usar la misma imagen de OpenJDK 21 para ejecutar la aplicación
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=build /app/target/forohub-0.0.1-SNAPSHOT.jar /app/forohub.jar

# Exponer el puerto en el que la aplicación se ejecuta
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/forohub.jar"]
