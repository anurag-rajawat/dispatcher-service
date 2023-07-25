FROM gcr.io/distroless/java17-debian11

WORKDIR workspace

COPY maven/build/libs/*.jar dispatcher-service.jar

EXPOSE 9003

CMD ["dispatcher-service.jar"]
