package de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation

import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.Controller.ControllerInterface
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Util.{
  UndoManager,
  Observable,
  Command,
  Which,
  Undo,
  Redo,
  Event
}
import scala.util.{Either, Left => Err, Right => Ok}

class Controller(using var game: GameInterface)
    extends ControllerInterface
    with Observable:
  override def toString(): String = game.toString()

  var first_? = true
  override val x = game.getWidth
  override val y = game.getHeight
  val undoManager = new UndoManager[GameInterface]

  override def handleTrigger(
      handlePosition: Position => Either[String, GameInterface],
      pos: Position
  ) =
    val result = handlePosition(pos) match
      case Err(msg)    => Event.Failure(msg)
      case Ok(newGame) => game = newGame; state
    notifyObservers(result)

  override def handleTrigger(undoRedo: () => Either[Which, GameInterface]) =
    val result = undoRedo() match
      case Ok(newGame) =>
        game = newGame; state
      case Err(failure) =>
        failure match
          case Undo => Event.Failure("Nothing to undo.")
          case Redo => Event.Failure("Nothing to redo.")
    notifyObservers(result)

  def removeMine(pos: Position) =
    first_? = false // do not call this function again
    val snapShot = game.getSnapShot
    game.restore(
      SnapShot(
        snapShot.openFields,
        snapShot.flaggedFields,
        snapShot.mines.excl(pos), // remove the mine at this position
        snapShot.state
      )
    )
  override def openField(pos: Position): Either[String, GameInterface] =
    game.canOpen_?(pos) match
      case InsertResult.Ok =>
        Ok(
          undoManager.doStep(
            if first_? then removeMine(pos) else game,
            ActionCommand(pos, Action.Open)
          )
        )
      case error => handleError(error)

  override def flagField(pos: Position) =
    game.canFlag_?(pos) match
      case InsertResult.Ok =>
        Ok(undoManager.doStep(game, ActionCommand(pos, Action.Flag)))
      case error => handleError(error)

  def handleError(err: InsertResult) =
    err match
      case InsertResult.NotInBounds =>
        Err(
          s"Not in bounds of width: ${x - 1} and height: ${y - 1}."
        )
      case InsertResult.AlreadyOpen =>
        Err("This field is already revealed.")
      case InsertResult.Flagged =>
        Err("This field has been flagged.")
      case InsertResult.Ok => Err("Unknown error occured")

  override def undo(): Either[Which, GameInterface] = undoManager.undoStep(game)
  override def redo(): Either[Which, GameInterface] = undoManager.redoStep(game)

  def state: Event =
    game.getSnapShot.state match
      case State.Won     => Event.Won
      case State.Lost    => Event.Lost
      case State.OnGoing => Event.Success

  override def symbolAt(pos: Position): Symbols =
    game.whichSymbol(pos)

  override def getAllPositions() =
    game.getAllPositions
