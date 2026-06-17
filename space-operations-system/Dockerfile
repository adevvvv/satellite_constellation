# Build stage
FROM gradle:8.8-jdk21 AS build
WORKDIR /app

# Копируем всё содержимое
COPY . .

# Даем права и проверяем
RUN chmod +x gradlew 2>/dev/null || true
RUN ls -la gradlew

# Загружаем зависимости (используем gradle вместо gradlew)
RUN gradle dependencies --no-daemon

# Собираем приложение
RUN gradle build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser
COPY --from=build /app/build/libs/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]