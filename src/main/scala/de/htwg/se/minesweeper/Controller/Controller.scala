package de.htwg.se.minesweeper.Controller

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

class Controller(var game: Game) extends Observable:
  override def toString(): String = game.toString()

  val x = game.bounds.width
  val y = game.bounds.height
  val undoManager = new UndoManager[Game]

  def handleTrigger(handlePosition: Position => InsertResult, pos: Position) =
    val result = handlePosition(pos) match
      case InsertResult.Success(success) => game = success; state
      case InsertResult.NotInBounds =>
        Event.Failure(
          s"Not in bounds of width: ${x - 1} and height: ${y - 1}."
        )
      case InsertResult.AlreadyOpen =>
        Event.Failure("This field is already revealed.")
      case InsertResult.Flagged =>
        Event.Failure("This field has been flagged.")
    notifyObservers(result)

  def handleTrigger(undoRedo: () => Either[Which, Game]) =
    val result = undoRedo() match
      case Ok(success) => game = success; state
      case Err(failure) =>
        failure match
          case Undo => Event.Failure("Nothing to undo.")
          case Redo => Event.Failure("Nothing to redo.")
    notifyObservers(result)

  def openField(pos: Position): InsertResult =
    game.canOpen_?(pos) match
      case true =>
        InsertResult.Success(
          undoManager.doStep(game, ActionCommand(pos, Action.Open))
        )
      case error: InsertResult => error

  def flagField(pos: Position): InsertResult =
    game.canFlag_?(pos) match
      case true =>
        InsertResult.Success(
          undoManager.doStep(game, ActionCommand(pos, Action.Flag))
        )
      case error: InsertResult => error

  def undo(): Either[Which, Game] = undoManager.undoStep(game)
  def redo(): Either[Which, Game] = undoManager.redoStep(game)

  def state: Event =
    game.state match
      case State.Won     => Event.Won
      case State.Lost    => Event.Lost
      case State.OnGoing => Event.Success

  def symbolAt(pos: Position): String =
    game.whichSymbol(pos)
    