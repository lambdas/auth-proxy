# syntax=docker/dockerfile:1

FROM openjdk:19-bullseye

WORKDIR /app

COPY build.sbt sbt ./
COPY project ./project
RUN ./sbt update

COPY src ./src

CMD ["./sbt", "run"]
