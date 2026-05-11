# Optimized Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the pre-built jar
COPY target/*.jar app.jar

EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
