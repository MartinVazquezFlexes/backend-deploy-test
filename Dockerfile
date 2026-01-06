FROM eclipse-temurin:17-jdk-jammy AS build
RUN apt-get update && apt-get install -y maven && apt-get clean

WORKDIR /app

# Copia primero solo el pom.xml para cachear dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Luego copia el código
COPY src ./src
RUN mvn clean package -DskipTests

FROM gcr.io/distroless/java17-debian11
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "/app/app.jar"]