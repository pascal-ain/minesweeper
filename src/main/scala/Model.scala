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
          if accum.contains(Position(x,y)) then randomPositions(iterations, accum)
          else randomPositions(iterations - 1, accum.incl(Position(x,y)))
        }

    Board(Set.empty, randomPositions(numberOfMines, Set.empty), Set.empty)
  override def toString(): String =
    var result = new StringBuilder
    for x <- Range.inclusive(0, this.width - 1) do
      for y <- 0 to (this.height - 1) do
        if this.board.openFields.contains(Position(x, y)) then
          result.append(" O ")
        else if this.board.flaggedFields.contains(Position(x, y)) then
          result.append(" F ")
        else result.append(" ▢ ")
      result.append(sys.props("line.separator"))
    result.toString()

  def flaggedFields(pos: Position): Set[Position] =
      if this.board.flaggedFields.contains(pos) then this.board.flaggedFields.excl(pos)
      else this.board.flaggedFields.incl(pos)
