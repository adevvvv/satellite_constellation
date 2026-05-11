# Build stage
FROM gradle:8.8-jdk21 AS build
WORKDIR /app

# Копируем конфигурационные файлы Gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle

# Создаем gradlew скрипт
RUN gradle wrapper --gradle-version 8.8

# Даем права на выполнение
RUN chmod +x gradlew

# Загружаем зависимости
RUN gradle dependencies --no-daemon

# Копируем исходный код
COPY src ./src

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