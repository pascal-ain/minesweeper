package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Model.*
import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine
import de.htwg.se.minesweeper.Util.{Observer, Event}
import de.htwg.se.minesweeper.Util.Which
import ParseInput.*

class REPL(
    controller: Controller,
    flagSymbol: String,
    mineSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends Observer
    with App:
  controller.add(this)

  val eol = sys.props("line.separator")
  var state = () => runREPL()
  def run() =
    print(gameString())
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
        print(gameString())
      }

  def gameString() =
    REPLSymbolsDecorator(
      controller,
      mineSymbol,
      flagSymbol,
      closedFieldSymbol,
      scoreSymbols
    )
      .toString()

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
