# Fase de construcción
FROM maven:3.8.5-openjdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

# Fase de ejecución
FROM adoptopenjdk:11-jre-hotspot
# Usamos un comodín (*) por si el nombre del JAR cambia ligeramente
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
