package de.htwg.se.minesweeper.Model
import scala.util.Random
import de.htwg.se.minesweeper.Field

// Data representation of the game board
final case class Board(
  openFields: Set[Position],
  mines: Set[Position],
  flaggedFields: Set[Position]
):
  def insertPosition(
    pos: Position,
    bounds: Bounds,
    field: Field
  ): Option[Board] =
    if pos.x >= bounds.width || pos.y >= bounds.height then None
    else
      field match
        case Field.Open =>
          if flaggedFields.contains(pos) then None
          else Some(copy(openFields = openFields.incl(pos)))
        case Field.Flag =>
          if openFields(pos) then None
          else if flaggedFields.contains(pos) then
            Some(copy(flaggedFields = flaggedFields.excl(pos)))
          else Some(copy(flaggedFields = flaggedFields.incl(pos)))

object Board:
  def apply(mines: Set[Position]) =
    new Board(Set.empty, mines, Set.empty)

def generateRandomPositions(howMany: Int, width: Int, height: Int) =
  require(width * height > howMany)
  def recur(iteration: Int, accum: Set[Position]): Set[Position] =
    iteration match
      case 0 => accum
      case _ => {
        val x = Random.between(0, width)
        val y = Random.between(0, height)
        if accum.contains(Position(x, y)) then recur(iteration, accum)
        else recur(iteration - 1, accum.incl(Position(x, y)))
      }
  recur(howMany, Set.empty)
