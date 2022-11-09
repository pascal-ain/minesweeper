package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.*

object Helper:
  def getAllPositions(game: Game): Iterator[Position] =
    (0 until game.bounds.height)
      .flatMap(y => (0 until game.bounds.width).map(x => Position(x, y)))
      .iterator

  def openFields(game: Game, toOpen: Iterator[Position]): Game =
    toOpen.foldLeft[Game](game)((iteration, pos) => iteration.canOpen(pos))

//  ¤  1  0  0  1  ¤  1  0  0  0
//  1  1  0  0  1  2  2  1  0  0
//  1  1  0  0  0  1  ¤  1  0  0
//  ¤  3  1  1  1  3  2  2  0  0
//  ¤  ¤  2  2  ¤  3  ¤  1  0  0
//  ¤  ¤  4  3  ¤  4  2  3  1  1
//  4  ¤  4  ¤  2  2  ¤  2  ¤  1
//  2  ¤  3  1  1  1  1  2  2  2
//  2  3  3  2  1  1  0  0  1  ¤
//  1  ¤  ¤  2  ¤  1  0  0  1  1
