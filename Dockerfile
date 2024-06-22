FROM openjdk:17
ARG JAR_FILE=target/*.jar
WORKDIR /home/ubuntu/services/billybang
VOLUME /var/log
COPY ${JAR_FILE} billybang-user-service.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/billybang-user-service.jar"]