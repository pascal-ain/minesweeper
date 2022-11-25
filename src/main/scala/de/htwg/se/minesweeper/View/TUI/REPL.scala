package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Model.*
import scala.util.{Either, Left => Err, Right => Ok}
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine
import de.htwg.se.minesweeper.Util.{Observer, Event, MyApp}
import de.htwg.se.minesweeper.Util.Which
import ParseInput.*

class REPL(
    val game: Game,
    mineSymbol: String,
    flagSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends MyApp(
      game,
      flagSymbol,
      mineSymbol,
      closedFieldSymbol,
      scoreSymbols
    ):
  val controller = Controller(game)
  controller.add(this)

  val eol = sys.props("line.separator")
  var state = () => runREPL()
  override def run() =
    print(gameString())
    runREPL()

  var parseStrategy: ParseStrategy = OnGoingStrategy(controller)

  override def update(e: Event): Unit =
    e match
      case Event.Failure(msg) =>
        state = () => {
          println(gameString() + eol + msg)
          runREPL()
        }
      case Event.Won => {
        parseStrategy = LostOrWonStrategy(controller)
        state = () => {
          println(gameString() + eol + "You won!")
          runREPL()
        }
      }
      case Event.Lost => {
        parseStrategy = LostOrWonStrategy(controller)
        state = () => {
          println(gameString() + eol + "You lost!")
          runREPL()
        }
      }
      case Event.Success => {
        parseStrategy = OnGoingStrategy(controller)
        state = () => {
          print(gameString())
          runREPL()
        }
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
    print(">> ")
    val input = readLine()
    parseStrategy.handleInput(input) match
      case Ok(operation: Operation) =>
        operation match
          case Operation.OpenOrFlag(function, pos) =>
            controller.handleTrigger(function, pos)
          case Operation.UndoRedoOrExit(function) =>
            controller.handleTrigger(function)
      case Err(msg: String) =>
        state = () => {
          println(gameString() + eol + msg)
          runREPL()
        }
    state()
