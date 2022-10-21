package Model

import scala.util.Random
import scala.collection.immutable.HashSet

case class Position(
    x: Int,
    y: Int
)

case class Board(
    openFields: HashSet[Position],
    mines: HashSet[Position],
    flaggedFields: HashSet[Position]
)

class Game(val width: Int = 9, val height: Int = 9):
  val minePercentage = 0.15
  val lost = false
  val board: Board = generateBoard

  private def randomPositions(iterations: Int, accum: HashSet[Position]): HashSet[Position] =
  iterations match
    case 0 => accum
    case _ => {
      val x = Random.between(0, this.width)
      val y = Random.between(0, this.height)
      if accum.contains(Position(x, y)) then
        randomPositions(iterations, accum)
      else randomPositions(iterations - 1, accum.incl(Position(x, y)))
    }

  private def generateBoard: Board =
    val numberOfMines =
      ((this.width * this.height) * minePercentage).floor.toInt
    Board(HashSet.empty, randomPositions(numberOfMines, HashSet.empty), HashSet.empty)

  override def toString(): String =
    (for
      x <- 0 until this.width
      y <- 0 until this.height
    yield
      if y == this.width - 1 then whichSymbol(Position(x, y))
      else whichSymbol(Position(x, y)))
    .mkString

  private def whichSymbol(pos: Position): String =
    this.board.openFields.contains(pos) match
      case true => if this.board.mines.contains(pos) then
        " B "
        else if this.board.flaggedFields.contains(pos) then
          " F "
        else " 0 "
      case false => " O "

  // TODO: implement a private constructor and return a game instead of a new set
  def flaggedFields(pos: Position): HashSet[Position] =
    if this.board.flaggedFields.contains(pos) then
      this.board.flaggedFields.excl(pos)
    else this.board.flaggedFields.incl(pos)