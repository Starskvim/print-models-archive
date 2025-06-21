FROM openjdk:21-jdk-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       iputils-ping \
       dnsutils \
       curl \
       net-tools \
    && rm -rf /var/lib/apt/lists/*

COPY /build/libs/print-models-archive-0.0.2-SNAPSHOT.jar PrintModelArchiveApp.jar
COPY src src
EXPOSE 8082
ENTRYPOINT ["java","-jar","/PrintModelArchiveApp.jar"]