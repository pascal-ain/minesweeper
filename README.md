![MAIN status](https://github.com/pascal-ain/minesweeper/actions/workflows/scala.yml/badge.svg)
[![Coverage Status](https://coveralls.io/repos/github/pascal-ain/minesweeper/badge.svg)](https://coveralls.io/github/pascal-ain/minesweeper)

# Scala 3 Minesweeper

## Building the project
This project is being developed by [Huan Thieu Nguyen](https://github.com/huanie) and [Pascal Scheck](https://github.com/pascal-ain) for the Software Engineering lecture at [HTWG Konstanz](https://www.htwg-konstanz.de/).

`sbt run` to run the project!

## How to play

`open <x,y>` to open a field \
`flag <x,y>` to flag a field \
`undo | redo` undo or redo your latest move \
`quit | q | exit` to end the game.

## Docker

For Windows you need to install [XMing](https://sourceforge.net/projects/xming/) \
For Mac you need to install [XQuartz](https://www.xquartz.org/) 
\
\
First build the image

`docker build -t minesweeper-image .`

then run it

`docker run -it --rm -v /tmp/.X11-unix:/tmp/.X11-unix --device /dev/dri -p 8080:8080 --privileged -v :/minesweeper minesweeper-image`

## Jar

To create an executable jar file you can use \
`sbt assembly`