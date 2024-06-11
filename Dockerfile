FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
CMD ["./gradlew", "clean", "build"]
VOLUME /tmp
COPY ${JAR_FILE} billybang-user-service.jar
EXPOSE 3000
ENTRYPOINT ["java","-jar","/billybang-user-service.jar"]