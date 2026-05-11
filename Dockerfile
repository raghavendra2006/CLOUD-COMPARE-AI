# Optimized Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the pre-built jar (frontend is already bundled inside by Jenkins)
COPY target/*.jar app.jar

EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
