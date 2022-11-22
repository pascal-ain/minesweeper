package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.*

object Helper:
  def getAllPositions(game: Game): Iterator[Position] =
    (0 until game.bounds.height)
      .flatMap(y => (0 until game.bounds.width).map(x => Position(x, y)))
      .iterator

  def openFields(game: Game, toOpen: Iterator[Position]): Game =
    toOpen.foldLeft[Game](game)((iteration, pos) => iteration.canOpen(pos))
