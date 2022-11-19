package de.htwg.se.minesweeper.View.TUI
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Controller.*
import scala.io.StdIn.readLine
import de.htwg.se.minesweeper.Util.{Observer, Event}

class REPL(controller: Controller) extends Observer:
  def this(game: Game) = this(new Controller(game))

  controller.add(this)

  val eol = sys.props("line.separator")
  var state = () => runREPL()

  def run() =
    print(game())
    runREPL()

  override def update(e: Event): Unit =
    e match
      case Event.InvalidPosition(msg) => println(game() + eol + msg)
      case Event.Won        => state = () => println(game() + eol + "You won!")
      case Event.Lost       => state = () => println(game() + eol + "You lost!")
      case Event.Success(_) => print(game())

  def game() =
    REPLSymbolsDecorator(controller, "💣", "🚩", "⬛", mineCount).toString()

  def mineCount(mines: Int) =
    s"${(0 until 9).map(num => s"${num.toString}\uFE0F\u20E3")(mines)} "

  def runREPL(): Unit =
    print(">> ")
    val input = readLine()
    if input.matches("q|quit|exit") then return
    parseInput(input) match
      case None =>
        println(
          s"${game()}${eol}Invalid command${eol}availabe commands:${eol}open <x,y>${eol}flag <x,y>${eol}quit, q or exit to end the game"
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
