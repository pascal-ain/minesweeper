package de.htwg.se.minesweeper.Model

import de.htwg.se.minesweeper.Field

// The state of the game
final case class Game(bounds: Bounds, lost: Boolean, board: Board):
  def openField(pos: Position): Game =
    if lost then this
    else
      val openFields = board.insertPosition(pos, bounds, Field.Open) match
        case None        => board
        case Some(value) => value
      if board.mines.contains(pos) then copy(board = openFields, lost = true)
      else copy(board = openFields)

  def flagField(pos: Position) =
    if lost then this
    else
      val flaggedFields = board.insertPosition(pos, bounds, Field.Flag) match
        case None        => board
        case Some(value) => value
      copy(board = flaggedFields)

  override def toString() =
    (for
      y <- 0 until bounds.height
      x <- 0 until bounds.width
    yield
      val pos = Position(x, y)
      if x == bounds.width - 1 then
        whichSymbol(pos) + sys.props("line.separator")
      else whichSymbol(pos)
    ).mkString

  def whichSymbol(pos: Position) =
    board.openFields.get(pos) match
      case Some(value) if board.mines.contains(pos) => " ¤ "
      case Some(value) =>
        " " + board.surroundingMines(pos, bounds).toString + " "
      case None =>
        if board.flaggedFields.contains(pos) then " F "
        else " O "

object Game:
  def apply(
      width: Int = 9,
      height: Int = 9,
      minePercentage: Double = 0.15
  ) =
    new Game(
      Bounds(width, height),
      false,
      Board(
        generateRandomPositions(
          (width * height * minePercentage).toInt,
          width,
          height
        )
      )
    )
