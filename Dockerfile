FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src src

RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN groupadd --system spring && useradd --system --gid spring spring

COPY --from=build /workspace/target/scheduler-base-0.0.1-SNAPSHOT.jar /app/app.jar
COPY docker/entrypoint.sh /app/entrypoint.sh
COPY docker/java.security.legacy /app/java.security.legacy

RUN chmod +x /app/entrypoint.sh

USER spring:spring

EXPOSE 8081

ENTRYPOINT ["/app/entrypoint.sh"]
