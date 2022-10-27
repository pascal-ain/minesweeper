package de.htwg.se.minesweeper.Model

import de.htwg.se.minesweeper.Field

// The state of the game
final case class Game(bounds: Bounds, lost: Boolean, board: Board):
  def this(width: Int = 9, height: Int = 9, minePercentage: Double = 0.15) =
    this(
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
  def openField(pos: Position) =
    val openFields = board.insertPosition(pos, bounds, Field.Open) match
      case None        => board
      case Some(value) => value
    copy(board = openFields)
  def flagField(pos: Position) =
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
    if board.openFields.contains(pos) && board.mines.contains(pos) then " ¤ "
    else if board.flaggedFields(pos) then " F "
    else if board.openFields.contains(pos) then " 0 "
    else " O "
