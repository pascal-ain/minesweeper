package de.htwg.se.minesweeper.Model
import scala.util.Random
import de.htwg.se.minesweeper.Field

// Data representation of the game board
final case class Board(
    openFields: Map[Position, Int],
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
          else
            Some(
              copy(openFields =
                openFields + (pos -> surroundingMines(pos, bounds))
              )
            )
        case Field.Flag =>
          if openFields.contains(pos) then None
          else if flaggedFields.contains(pos) then
            Some(copy(flaggedFields = flaggedFields.excl(pos)))
          else Some(copy(flaggedFields = flaggedFields.incl(pos)))

  def neighbors(pos: Position, bounds: Bounds): Iterator[Position] =
    val (x, y) = (pos.x, pos.y)
    val (width, height) = (bounds.width, bounds.height)
    /* from max(x - 1, 1) - 1 to min(x + 1, width - 1)
     example: 1,1 so it generates 0,_ to 2,_ only x is generated!
     same principle with y
     flatmap is required because otherwise a Vector(Range, Range) will be created
     then for one x it goes over each y to make the position like a nested for loop
     */
    (x.max(1) - 1 to (x + 1).min(width - 1))
      .flatMap(posx =>
        (y.max(1) - 1 to (y + 1).min(height - 1)).map(posy =>
          Position(posx, posy)
        )
      )
      .filterNot(_ == pos)
      .iterator // exclude input pos

  def surroundingMines(pos: Position, bounds: Bounds) =
    neighbors(pos, bounds).count(mines.contains(_))

object Board:
  def apply(mines: Set[Position]) =
    new Board(Map.empty, mines, Set.empty)

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
