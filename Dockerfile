FROM eclipse-temurin:22-jdk as builder
WORKDIR /app

COPY . .

RUN chmod +x mvnw && \
    ./mvnw clean package -DskipTests

FROM eclipse-temurin:22-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]