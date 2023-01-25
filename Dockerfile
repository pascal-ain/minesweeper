FROM openjdk:11
ENV DISPLAY=host.docker.internal:0.0
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6
WORKDIR /app
COPY Minesweeper-assembly-0.1.0-SNAPSHOT.jar bin/
EXPOSE 8080
CMD java -jar bin/Minesweeper-assembly-0.1.0-SNAPSHOT.jar
# CMD sbt run
