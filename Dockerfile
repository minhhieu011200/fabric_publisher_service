FROM openjdk:17-alpine
WORKDIR /app
COPY target/*.jar /app/fabric_publisher.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/fabric_publisher.jar"]