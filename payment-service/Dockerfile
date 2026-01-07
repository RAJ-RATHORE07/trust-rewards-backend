# ---------- BUILD STAGE ----------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy full multi-module project
COPY . .

# Build ONLY payment-service + dependencies
RUN mvn -pl payment-service -am clean package -DskipTests

# ---------- RUN STAGE ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/payment-service/target/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]
