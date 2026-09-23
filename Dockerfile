# Stage 1: Build Frontend (React + Vite)
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend
COPY cloudcompare-frontend/package*.json ./
RUN npm ci
COPY cloudcompare-frontend/ ./
RUN npm run build

# Stage 2: Build Backend (Spring Boot + Java 21)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app
COPY pom.xml ./
COPY src ./src
# Copy compiled frontend assets to Spring Boot static/react resources
COPY --from=frontend-builder /app/frontend/dist ./src/main/resources/static/react
RUN mvn clean package -DskipTests

# Stage 3: Optimized Runtime Image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=backend-builder /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
