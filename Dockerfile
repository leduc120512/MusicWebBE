# syntax=docker/dockerfile:1
#
# Two stages: build the jar with a full JDK, run it on a JRE.
# The jar is unpacked into Spring Boot layers so a code-only change rebuilds
# roughly 200 kB instead of the whole 60 MB fat jar.

# ----------------------------------------------------------------- build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Dependencies first: this layer is reused whenever pom.xml is unchanged
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package \
 && java -Djarmode=layertools -jar target/music-api-1.0.0.jar extract --destination target/layers

# --------------------------------------------------------------- runtime ----
FROM eclipse-temurin:17-jre-jammy AS runtime

RUN useradd --system --create-home --uid 10001 spring
WORKDIR /app

COPY --from=build --chown=spring:spring /build/target/layers/dependencies/ ./
COPY --from=build --chown=spring:spring /build/target/layers/spring-boot-loader/ ./
COPY --from=build --chown=spring:spring /build/target/layers/snapshot-dependencies/ ./
COPY --from=build --chown=spring:spring /build/target/layers/application/ ./

# Uploads need a writable path. On Render free instances this is ephemeral -
# mount a disk and set UPLOAD_DIR to it if the files have to survive a redeploy.
RUN mkdir -p /tmp/upload && chown spring:spring /tmp/upload
ENV UPLOAD_DIR=/tmp/upload

# Container images default to UTC; match the timezone the app writes dates in
ENV TZ=Asia/Ho_Chi_Minh

USER spring

# Documentation only - the platform decides the real port through $PORT
EXPOSE 8082

# Free tier is 512 MB, so cap the heap and use the cheapest collector
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -XX:TieredStopAtLevel=1"

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
