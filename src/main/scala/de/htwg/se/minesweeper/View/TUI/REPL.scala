package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine
import de.htwg.se.minesweeper.Util.{Observer, Event}

class REPL(controller: Controller) extends Observer:
  def this(game: Game) = this(new Controller(game))

  controller.add(this)

  val eol = sys.props("line.separator")
  var onGoing = true

  def run() =
    print(controller.game.toString)
    runREPL()

  override def update(e: Event): Unit =
    e match
      case Event.InvalidPosition(msg) => println(game() + eol + msg)
      case Event.Won  => println(game() + eol + "You won!"); onGoing = false
      case Event.Lost => println(game() + eol + "You lost!"); onGoing = false
      case Event.Success(_) => print(game())

  def game() = controller.toString

  def runREPL(): Unit =
    print(">> ")
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
      case Some(operation) => operation.apply()
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

  def insertPosition(tokens: Array[String]): Option[() => Unit] =
    val coords = tokens(1).split(",").map(_.toInt)
    val pos = Position(coords(0), coords(1))
    tokens(0) match
      case "open" => Some(() => controller.openField(pos))
      case "flag" => Some(() => controller.flagField(pos))
      case _      => None
