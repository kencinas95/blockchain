FROM maven:3.8.6-openjdk-11-slim AS build
WORKDIR /home/app
COPY . .
RUN mvn clean package

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /home/app/target/*.jar application.jar
CMD java -jar application.jar --spring.profiles.active=docker
