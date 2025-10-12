# --- build ---
FROM gradle:8.10-jdk21 AS build
WORKDIR /workspace
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
# getting dependencies
RUN ./gradlew --no-daemon build -x test || true
COPY src ./src
# building
RUN ./gradlew --no-daemon clean bootJar

# --- run ---
FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV TZ=UTC JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
WORKDIR /app
COPY --from=build /workspace/build/libs/trainingapp.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
