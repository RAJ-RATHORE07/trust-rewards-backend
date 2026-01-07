# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy ONLY audit-service files
COPY audit-service/pom.xml pom.xml
COPY audit-service/src src

# Build ONLY this service
RUN mvn clean package -DskipTests

# ---------- Run stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8086
ENTRYPOINT ["java","-jar","app.jar"]
