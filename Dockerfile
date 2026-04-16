# Fase de construcción
FROM maven:3.8.5-openjdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

# Fase de ejecución
FROM adoptopenjdk:11-jre-hotspot
COPY --from=build /target/Codigo_TorneApp-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
