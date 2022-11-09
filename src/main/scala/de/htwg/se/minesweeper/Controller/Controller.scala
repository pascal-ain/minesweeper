package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Util.Observable
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Util.Event
import de.htwg.se.minesweeper.Model.State

class Controller(var game: Game) extends Observable:
  override def toString(): String = game.toString()

  def flagField(pos: Position) =
    notifyObservers(validatePosition(pos, Field.Flag))

  def validatePosition(
      pos: Position,
      operation: Field
  ): Event.InvalidPosition | Event.Success =
    val (x, y) = (game.bounds.width, game.bounds.height)
    val newGame = operation match
      case Field.Open => game.openField(pos)
      case Field.Flag => game.flagField(pos)
    newGame match
      case InsertResult.Success(value) => game = value; Event.Success(value)
      case InsertResult.NotInBounds =>
        Event.InvalidPosition(s"Not in bounds of width: $x and height: $y")
      case InsertResult.AlreadyOpen =>
        Event.InvalidPosition(s"This field is already revealed")

  def openField(pos: Position) =
    val openResult = validatePosition(pos, Field.Open)
    openResult match
      case _: Event.Success => notifyObservers(state)
      case _                => notifyObservers(openResult)

  def state =
    game.state match
      case State.Won     => Event.Won
      case State.Lost    => Event.Lost
      case State.OnGoing => Event.Success(game)
