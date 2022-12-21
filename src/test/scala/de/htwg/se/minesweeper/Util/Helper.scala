package de.htwg.se.minesweeper.Util

import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.*
import de.htwg.se.minesweeper.Model.*

object Helper:
  def getAllPositions(using game: GameInterface): Iterator[Position] =
    (0 until game.getHeight)
      .flatMap(y => (0 until game.getWidth).map(x => Position(x, y)))
      .iterator

  def openFields(using
      game: Game,
      toOpen: Iterator[Position]
  ): Game =
    toOpen.foldLeft[Game](game)((iteration, pos) => iteration.openField(pos))
