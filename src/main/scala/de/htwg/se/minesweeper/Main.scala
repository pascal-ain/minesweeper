package de.htwg.se.minesweeper

import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.View.TUI.*

// $COVERAGE-OFF$Disabling highlighting by default until a workaround for https://issues.scala-lang.org/browse/SI-8596 is found
val eol = sys.props("line.separator")

@main def run(difficulty: String*): Unit =
  val msg =
    "start the program with <easy> for 9x9 board" + eol + "<medium> for 16x16 board" + eol + "<hard> for 30x16 board" + eol
  if difficulty.length != 0 || difficulty.length > 1 then print(msg)
  else if difficulty.isEmpty then new REPL(Game(9, 9, 0.15)).run()
  else
    difficulty.apply(0) match
      case "easy"   => new REPL(Game()).run()
      case "medium" => new REPL(Game(16, 16, 0.15)).run()
      case "hard"   => new REPL(Game(30, 16, 0.15)).run()
      case _        => print(msg)
// $COVERAGE-ON$
