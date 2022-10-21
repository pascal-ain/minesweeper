package Model

import scala.util.Random

case class Position(
    x: Int,
    y: Int
)

case class Board(
    openFields: Set[Position],
    mines: Set[Position],
    flaggedFields: Set[Position]
)

class Game(val width: Int = 9, val height: Int = 9):
  val minePercentage = 0.15
  val lost = false
  val board: Board = generateBoard
  def generateBoard: Board =
    val numberOfMines =
      ((this.width * this.height) * minePercentage).floor.toInt
    def randomPositions(iterations: Int, accum: Set[Position]): Set[Position] =
      iterations match
        case 0 => accum
        case _ => {
          val x = Random.between(0, this.width)
          val y = Random.between(0, this.height)
          if accum.contains(Position(x, y)) then
            randomPositions(iterations, accum)
          else randomPositions(iterations - 1, accum.incl(Position(x, y)))
        }
    Board(Set.empty, randomPositions(numberOfMines, Set.empty), Set.empty)

  // TODO: cleanup!
  override def toString(): String =
    val fields = for
      x <- 0 until this.width
      y <- 0 until this.height
    yield (x, y)
    fields
      .map(pos => {
        def whichSymbol(pos: Position) = if this.board.openFields.contains(pos)
        then
          if this.board.mines.contains(pos) then " B "
          // TODO: get the number of surrounding mines
          else " O "
        else if this.board.flaggedFields.contains(pos) then " F "
        else " C "
        pos match
          case (x, y) if y == (this.width - 1) =>
            whichSymbol(Position(x, y)) + sys.props("line.separator")
          case (x, y) => whichSymbol(Position(x, y))
      })
      .mkString

  // TODO: implement a private constructor and return a game instead of a new set
  def flaggedFields(pos: Position): Set[Position] =
    if this.board.flaggedFields.contains(pos) then
      this.board.flaggedFields.excl(pos)
    else this.board.flaggedFields.incl(pos)
