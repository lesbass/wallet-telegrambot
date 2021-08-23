FROM gradle:jdk14 AS build-env
ADD . /app
WORKDIR /app
RUN gradle shadowJar

FROM openjdk:14-jdk-slim
COPY --from=build-env /app/build/libs/wallet-telegrambot.jar /app/wallet-telegrambot.jar
WORKDIR /app
RUN touch .env
ENTRYPOINT ["java", "-Xms1G", "-jar", "wallet-telegrambot.jar"]