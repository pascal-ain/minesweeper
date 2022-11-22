package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Util.Observable
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Util.Event
import de.htwg.se.minesweeper.Model.State

class Controller(var game: Game) extends Observable:
  override def toString(): String = game.toString()

  val x = game.bounds.width
  val y = game.bounds.height

  def handleTrigger(handlePosition: Position => InsertResult, pos: Position) =
    val result = handlePosition(pos) match
      case InsertResult.Success(value) => game = value; state
      case InsertResult.NotInBounds =>
        Event.InvalidPosition(
          s"Not in bounds of width: ${x - 1} and height: ${y - 1}"
        )
      case InsertResult.AlreadyOpen =>
        Event.InvalidPosition("This field is already revealed")
      case InsertResult.Flagged =>
        Event.InvalidPosition("This field has been flagged")
    notifyObservers(result)

  def openField(pos: Position): InsertResult =
    game.openField(pos)
  def flagField(pos: Position): InsertResult =
    game.flagField(pos)

  def state: Event =
    game.state match
      case State.Won     => Event.Won
      case State.Lost    => Event.Lost
      case State.OnGoing => Event.Success(game)
