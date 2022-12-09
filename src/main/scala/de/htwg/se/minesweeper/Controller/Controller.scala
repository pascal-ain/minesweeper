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

  def handleTrigger(
      handlePosition: Position => Either[String, Game],
      pos: Position
  ) =
    val result = handlePosition(pos) match
      case Err(msg)    => Event.Failure(msg)
      case Ok(newGame) => game = newGame; state
    notifyObservers(result)

  def handleTrigger(undoRedo: () => Either[Which, Game]) =
    val result = undoRedo() match
      case Ok(newGame) => game = newGame; state
      case Err(failure) =>
        failure match
          case Undo => Event.Failure("Nothing to undo.")
          case Redo => Event.Failure("Nothing to redo.")
    notifyObservers(result)

  def openField(pos: Position): Either[String, Game] =
    game.canOpen_?(pos) match
      case InsertResult.Ok =>
        Ok(undoManager.doStep(game, ActionCommand(pos, Action.Open)))
      case error => handleError(error)

  def flagField(pos: Position) =
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

  def undo(): Either[Which, Game] = undoManager.undoStep(game)
  def redo(): Either[Which, Game] = undoManager.redoStep(game)

  def state: Event =
    game.state match
      case State.Won     => Event.Won
      case State.Lost    => Event.Lost
      case State.OnGoing => Event.Success

  def symbolAt(pos: Position): Symbols =
    game.whichSymbol(pos)
  def getAllPositions() =
    game.board.getAllPositions
