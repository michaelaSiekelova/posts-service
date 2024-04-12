# Stage 1: MongoDB
FROM mongo AS mongo-stage
COPY init-db.js /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/init-db.js

# Stage 2: Java Application
#FROM openjdk:17-jdk-slim AS java-stage
#ARG JAR_FILE
#COPY target/posts-service-0.0.1-SNAPSHOT.jar /app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
