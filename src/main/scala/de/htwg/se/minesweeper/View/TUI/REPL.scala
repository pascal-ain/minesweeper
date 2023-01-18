package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Model.*
import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerInterface
import scala.io.StdIn.readLine
import de.htwg.se.minesweeper.Util.{Observer, Event}
import de.htwg.se.minesweeper.Util.Which
import ParseInput.*

class REPL(using
    controller: ControllerInterface
) extends Observer:
  controller.add(this)

  val eol = sys.props("line.separator")

  def run =
    println(gameString())
    runREPL()

  var parseState: ParseState = OnGoingState(controller)

  override def update(e: Event): Unit =
    e match
      case Event.Failure(msg) => println(gameString() + eol + msg)
      case Event.Won => {
        parseState = LostOrWonState(controller)
        println(gameString() + eol + "You won!")
      }
      case Event.Lost => {
        parseState = LostOrWonState(controller)
        println(gameString() + eol + "You lost!")
      }
      case Event.Success => {
        parseState = OnGoingState(controller)
        println(gameString())
      }
      case Event.Loading(width, height, event) => update(event)

  def gameString() =
    eol + REPLSymbolsDecorator(
      controller,
      "[*]",
      "[F]",
      "[ ]",
      (x: Int) => s"[$x]"
    ).toString

  def runREPL(): Unit =
    val input = readLine()
    parseState.handleInput(input) match
      case Ok(operation: Operation) =>
        operation match
          case Operation.OpenOrFlag(function, pos) =>
            controller.handleTrigger(function, pos)
          case Operation.UndoRedoOrExit(function) =>
            controller.handleTrigger(function)
      case Err(msg: String) => println(gameString() + eol + msg)
    runREPL()
