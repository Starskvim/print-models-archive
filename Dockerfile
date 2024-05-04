FROM openjdk:21-jdk-slim
COPY /build/libs/print-models-archive-0.0.2-SNAPSHOT.jar PrintModelArchiveApp.jar
COPY src src
EXPOSE 8082
ENTRYPOINT ["java","-jar","/PrintModelArchiveApp.jar"]