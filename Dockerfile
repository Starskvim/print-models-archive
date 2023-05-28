FROM openjdk:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
COPY /build/libs/print-models-archive-0.0.1-SNAPSHOT.jar PrintModelArchiveApp.jar
COPY src src
EXPOSE 8082
ENTRYPOINT ["java","-jar","/PrintModelArchiveApp.jar"]