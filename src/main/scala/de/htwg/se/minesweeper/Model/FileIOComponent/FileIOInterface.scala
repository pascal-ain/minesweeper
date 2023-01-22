package de.htwg.se.minesweeper.Model.FileIOComponent

import java.io.File
import scala.util.Try
import de.htwg.se.minesweeper.Model.GameComponent.*
import de.htwg.se.minesweeper.Config
import scala.util.{Left => Err, Right => Ok}

trait FileIOInterface {
  def load(path: File): Try[GameInterface]
  def save(game: GameInterface, path: File): Unit

  // The function only looks long because of the functions defined in it.
  // The functions are defined there because the interface should not expose them.
  // All the function is doing is verifying all the contraints.
  def verifyData(
      width: Int,
      height: Int,
      openFields: Set[Position],
      flaggedFields: Set[Position],
      mines: Set[Position]
  ): Either[String, GameInterface] = {
    def checkEveryConstraint(
        constraints: List[() => Option[String]]
    ): Option[String] = {
      constraints.headOption match
        case None => None
        case Some(constraint) => {
          val result = constraint()
          if result.isDefined then result
          else checkEveryConstraint(constraints.tail)
        }
    }

    def outOfBounds_?(fields: Set[Position]) =
      () =>
        fields.find(pos => pos.x >= width || pos.y >= height) match
          case None => None
          case Some(value) =>
            Some(
              s"Position(${value.x},${value.y}) is out of bounds of Bounds(${width},${height})"
            )

    val result = checkEveryConstraint(
      List(
        outOfBounds_?(mines),
        outOfBounds_?(flaggedFields),
        outOfBounds_?(openFields),
        // is there an intersection between openFields and flaggedFields?
        () => {
          if flaggedFields.intersect(openFields).isEmpty then None
          else Some("Flagged fields intersect with open fields!")
        }
      )
    )
    result match
      case None => {
        // make a game without openFields
        val game: GameInterface = Config
          .newGame(width, height, 0)
          .restore(SnapShot(Map.empty, flaggedFields, mines, State.OnGoing))
        // open the fields in case someone changed the mine positions
        Ok(
          openFields.foldLeft[GameInterface](game)(
            (iteration: GameInterface, pos: Position) =>
              iteration.openField(pos)
          )
        )
      }
      case Some(value) => Err(value)
  }
}
