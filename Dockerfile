FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/fondos-0.0.1-SNAPSHOT.jar app.jar

RUN addgroup -S btg && adduser -S btg -G btg

USER btg

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]