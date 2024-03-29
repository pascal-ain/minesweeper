package de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation

import scala.util.Random
import de.htwg.se.minesweeper.Model.GameComponent.{Position, Mine}

// Data representation of the game board
final case class Board(
    openFields: Map[Position, Int | Mine.type],
    mines: Set[Position],
    flaggedFields: Set[Position],
    bounds: Bounds
):

  def getSurroundingPositions(pos: Position): Iterator[Position] =
    val (x, y) = (pos.x, pos.y)
    val (width, height) = (bounds.width, bounds.height)
    /* from max(x - 1, 1) - 1 to min(x + 1, width - 1)
     example: 1,1 so it generates 0,_ to 2,_ only x is generated!
     same principle with y
     flatmap is required because otherwise a Vector(Range, Range) will be created
     then for one x it goes over each y to make the position like a nested for loop
     */
    (for { 
      posx <- (x.max(1) - 1 to (x + 1).min(width - 1))
      posy <- y.max(1) - 1 to (y + 1).min(height - 1)
      if Position(posx, posy) != pos
    } yield (Position(posx, posy))).iterator

  def surroundingMines(pos: Position) =
    getSurroundingPositions(pos).count(mines.contains(_))

  def getAllPositions =
    (for {
      posy <- (0 until bounds.height)
      posx <- (0 until bounds.width)
    } yield (Position(posx, posy))).iterator

object Board:
  def apply(mines: Int, bounds: Bounds) =
    generateRandomPositions(mines, bounds)

def generateRandomPositions(howMany: Int, bounds: Bounds) =
  val (width, height) = (bounds.width, bounds.height)
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
  new Board(Map.empty, recur(howMany, Set.empty), Set.empty, bounds)
