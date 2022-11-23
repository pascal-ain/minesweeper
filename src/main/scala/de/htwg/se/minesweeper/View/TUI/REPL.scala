package de.htwg.se.minesweeper.View.TUI

import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine
import de.htwg.se.minesweeper.Util.{Observer, Event, MyApp}

class REPL(
    controller: Controller,
    mineSymbol: String,
    flagSymbol: String,
    closedFieldSymbol: String,
    scoreSymbols: Int => String
) extends MyApp(
      controller,
      flagSymbol,
      mineSymbol,
      closedFieldSymbol,
      scoreSymbols
    ):
  controller.add(this)

  val eol = sys.props("line.separator")
  var state = () => runREPL()
  override def run() =
    print(gameString())
    runREPL()

  override def update(e: Event): Unit =
    e match
      case Event.Failure(msg) => println(gameString() + eol + msg)
      case Event.Won  => state = () => println(gameString() + eol + "You won!")
      case Event.Lost => state = () => println(gameString() + eol + "You lost!")
      case Event.Success(_) => print(gameString())

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
    if input.matches("q|quit|exit") then return
    parseInput(input) match
      case None =>
        println(
          s"${gameString()}${eol}Invalid command${eol}availabe commands:${eol}open <x,y>${eol}flag <x,y>${eol}quit, q or exit to end the gameString"
        )
      case Some(operation, pos) => controller.handleTrigger(operation, pos)
    state()

  def parseInput(input: String) =
    val userInput = input.toLowerCase().trim()
    if userInput.matches("((open|flag)\\s+\\d+,\\d+)") then
      val tokens = userInput.split("\\s+")
      val coords = tokens(1).split(",").map(_.toInt)
      val pos = Position(coords(0), coords(1))
      tokens(0) match
        case "open" => Some(controller.openField, pos)
        case "flag" => Some(controller.flagField, pos)
    else None
