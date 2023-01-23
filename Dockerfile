FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0
ENV DISPLAY=host.docker.internal:0.0
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6
WORKDIR /minesweeper
ADD . /minesweeper
COPY target/scala-3.2.2-RC2/Minesweeper-assembly-0.1.0-SNAPSHOT.jar target/
EXPOSE 8080
#CMD java -jar /minesweeper/Minesweeper-assembly-0.1.0-SNAPSHOT.jar
CMD java -jar target/Minesweeper-assembly-0.1.0-SNAPSHOT.jar
# CMD sbt run
