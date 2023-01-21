# only works on Windows
# https://sourceforge.net/projects/xming/ --> install this for X Server (needed to run)

# docker build -t minesweeper-image .
# docker run -ti minesweeper-image
# docker run -it --rm -v /tmp/.X11-unix:/tmp/.X11-unix --device /dev/dri -p 8080:8080 --privileged -v :/minesweeper minesweeper-image

FROM sbtscala/scala-sbt:eclipse-temurin-17.0.4_1.7.1_3.2.0
ENV DISPLAY=host.docker.internal:0.0
RUN apt-get update && \
    apt-get install -y libxrender1 libxtst6 libxi6
WORKDIR /minesweeper
ADD . /minesweeper
EXPOSE 8080
CMD sbt run
