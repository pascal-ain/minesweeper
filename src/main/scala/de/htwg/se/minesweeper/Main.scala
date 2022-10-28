package de.htwg.se.minesweeper

import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.View.TUI.*

// $COVERAGE-OFF$Disabling highlighting by default until a workaround for https://issues.scala-lang.org/browse/SI-8596 is found
val eol = sys.props("line.separator")

@main def run(difficulty: String*): Unit =
  val msg =
    "start the program with <easy> for 9x9 board" + eol + "<medium> for 16x16 board" + eol + "<hard> for 30x16 board" + eol
  if difficulty.length != 0 || difficulty.length > 1 then print(msg)
  else if difficulty.isEmpty then
    val game = new Game(9, 9, 0.15)
    print(game)
    repl(game)
  else
    difficulty.apply(0) match
      case "easy"   => repl(new Game())
      case "medium" => repl(new Game(16, 16, 0.15))
      case "hard"   => repl(new Game(30, 16, 0.15))
      case _        => print(msg)
// $COVERAGE-ON$
