package de.htwg.se.minesweeper

import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.View.GUI.GUI
import de.htwg.se.minesweeper.View.TUI.REPL

// $COVERAGE-OFF$
@main def run: Unit =
  new GUI().run
  new REPL().run

// $COVERAGE-ON$
