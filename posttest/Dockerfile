# stage build
FROM amazoncorretto:17.0.9-alpine3.18 AS builder
WORKDIR /app
ADD . .
RUN ["./gradlew", "clean", "bootJar"]

# stage running image
FROM gcr.io/distroless/java17-debian12:latest
WORKDIR /app
COPY --from=builder /app/build/libs/posttest-0.0.1-SNAPSHOT.jar posttest-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "posttest-0.0.1-SNAPSHOT.jar"]