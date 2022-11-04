package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.*

object Helper:
  def getAllPositions(game: Game): Iterator[Position] =
    (0 until game.bounds.height)
      .flatMap(y => (0 until game.bounds.height).map(x => Position(x, y)))
      .iterator
  def openFields(game: Game, toOpen: Iterator[Position]): Game =
    toOpen.nextOption() match
      case None      => game
      case Some(pos) => openFields(game.openField(pos), toOpen)
