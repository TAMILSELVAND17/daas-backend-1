 FROM maven:3.9.6-eclipse-temurin-17 AS build

 WORKDIR /app

 COPY pom.xml .
 RUN mvn dependency:go-offline

 COPY src ./src
 RUN mvn clean package -DskipTests

 # === Run Stage ===
 FROM eclipse-temurin:17-jdk-alpine

 WORKDIR /app

 COPY --from=build /app/target/DaasComputers-0.0.1-SNAPSHOT.jar .

 EXPOSE 8081

 ENTRYPOINT ["java", "-jar", "/app/DaasComputers-0.0.1-SNAPSHOT.jar"]