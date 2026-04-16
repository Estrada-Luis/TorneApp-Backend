# Fase de compilación
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean install -DskipTests

# Fase de ejecución (Imagen corregida)
FROM eclipse-temurin:17-jdk
COPY --from=build /target/Codigo_TorneApp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
