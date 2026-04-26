# Build stage
FROM gradle:8.8-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY --chown=gradle:gradle . .
RUN gradle dependencies --refresh-dependencies && \
    gradle build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

COPY --from=build /app/build/libs/*.jar app.jar

USER appuser
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]