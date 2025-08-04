# ----------- Build Stage -----------
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app
COPY . .

# Build the app and detect final jar name
RUN mvn clean package -DskipTests && \
    JAR_NAME=$(mvn help:evaluate -Dexpression=project.build.finalName -q -DforceStdout) && \
    cp target/$JAR_NAME.jar app.jar

# ----------- Runtime Stage -----------
FROM eclipse-temurin:21

WORKDIR /app

COPY --from=build /app/app.jar app.jar

ENV PORT=8089
EXPOSE 8089

ENTRYPOINT ["java", "-jar", "app.jar"]









#  FROM maven:3.9.6-eclipse-temurin-17 AS build
#
#  WORKDIR /app
#
#  COPY pom.xml .
#  RUN mvn dependency:go-offline
#
#  COPY src ./src
#  RUN mvn clean package -DskipTests
#
#  # === Run Stage ===
#  FROM eclipse-temurin:17-jdk-alpine
#
#  WORKDIR /app
#
#  COPY --from=build /app/target/DaasComputers-0.0.1-SNAPSHOT.jar .
#
#  EXPOSE 8081
#
#  ENTRYPOINT ["java", "-jar", "/app/DaasComputers-0.0.1-SNAPSHOT.jar"]