package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine

def repl(game: Game): Unit =
  if game.lost then println("You lost!")
  else
    print(">> ")
<<<<<<< Updated upstream
    parse(readLine(), game) match
      case Some(x) => {
        print(x)
        repl(x)
      }
      case None => {
        println(game.toString() + "invalid command")
        repl(game)
      }
=======
    parseInput(readLine()) match
      case None =>
        println(
          game() + eol
            + "Invalid command" + eol
            + "availabe commands:" + eol
            + "open <x,y>" + eol
            + "flag <x,y>" + eol
            + "exit"
        )
      case Some(pos, operation) => controller.handleTrigger(operation, pos)
    if onGoing then runREPL()

  def parseInput(input: String) =
    val userInput = input.toLowerCase().trim()
    if userInput.matches("((open|flag)\\s+\\d+,\\d+)|exit") then
      handleTokens(userInput.split("\\s+"))
    else None

  def handleTokens(tokens: Array[String]) =
    tokens(0) match
      case "open" | "flag" => insertPosition(tokens)
      case "exit"          => sys.exit()
      case _               => None

  def insertPosition(
      tokens: Array[String]
  ): Option[(Position, Position => InsertResult)] =
    val coords = tokens(1).split(",").map(_.toInt)
    val pos = Position(coords(0), coords(1))
    tokens(0) match
      case "open" => Some(pos, controller.openField)
      case "flag" => Some(pos, controller.flagField)
      case _      => None
>>>>>>> Stashed changes
