package de.htwg.se.minesweeper

import de.htwg.se.minesweeper.Model.Game
import de.htwg.se.minesweeper.View.TUI.*

// $COVERAGE-OFF$

@main def run: Unit =
  new REPL(Game()).run()

// $COVERAGE-ON$
