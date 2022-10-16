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
    val numberOfMines = ((this.width*this.height) * minePercentage).floor.toInt
    def randomPositions(iterations: Int, accum: Set[Position]): Set[Position] =
      if iterations == 0 then accum
      else
        val x = Random.between(0, this.width + 1)
        val y = Random.between(0, this.height + 1)
        randomPositions(iterations - 1, accum.incl(Position(x,y)))
    Board(Set.empty, randomPositions(numberOfMines, Set.empty), Set.empty)
  override def toString(): String = 
    var result = new StringBuilder
    for x <- Range.inclusive(0, this.width) do
      for y <- Range.inclusive(0, this.height) do
        if this.board.openFields.contains(Position(x,y)) then result.append(" O ")
        else if this.board.flaggedFields.contains(Position(x,y)) then result.append(" F ")
        else result.append(" ▢ ")
    result.toString()