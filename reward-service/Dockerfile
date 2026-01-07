# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1️⃣ Copy common-events first and build it
COPY common-events/pom.xml common-events/pom.xml
COPY common-events/src common-events/src
RUN mvn -f common-events/pom.xml clean install -DskipTests

# 2️⃣ Copy reward-service and build it
COPY reward-service/pom.xml reward-service/pom.xml
RUN mvn -f reward-service/pom.xml dependency:go-offline

COPY reward-service/src reward-service/src
RUN mvn -f reward-service/pom.xml clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/reward-service/target/*.jar app.jar

EXPOSE 8084
ENTRYPOINT ["java","-jar","app.jar"]
