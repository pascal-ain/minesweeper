package de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation

import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.Config
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import de.htwg.se.minesweeper.Model.GameComponent.*
import de.htwg.se.minesweeper.Util.*
import scala.util.{Either, Left => Err, Right => Ok, Failure, Success}
import java.io.File

class Controller(using var game: GameInterface)
    extends ControllerInterface
    with Observable:
  override def toString(): String = game.toString()

  override def newGame(width: Int, height: Int, mines: Double) =
    this.game = Config.newGame(width, height, mines)
    undoManager = new UndoManager[GameInterface]
    notifyObservers(Event.Success)

  var first_? = true
  override def x = game.getWidth
  override def y = game.getHeight
  var undoManager = new UndoManager[GameInterface]

  override def handleTrigger(
      handlePosition: Position => Either[String, GameInterface],
      pos: Position
  ) =
    val result = handlePosition(pos) match
      case Err(msg)    => Event.Failure(msg)
      case Ok(newGame) => game = newGame; state
    notifyObservers(result)

  override def save(path: File, implementation: FileIOInterface) =
    implementation.save(this.game, path)

  override def load(path: File, implementation: FileIOInterface) =
    implementation.load(path) match
      case Success(value) => {
        this.undoManager = new UndoManager[GameInterface]
        this.game = value
        notifyObservers(Event.Loading(game.getWidth, game.getHeight, state))
      }
      case Failure(e) => notifyObservers(Event.Failure(e.toString()))

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
