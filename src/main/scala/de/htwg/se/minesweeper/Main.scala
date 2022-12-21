package de.htwg.se.minesweeper

import scala.util.{Success, Failure}
import de.htwg.se.minesweeper.Config.{given}
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation
import de.htwg.se.minesweeper.View.GUI.GUI
import de.htwg.se.minesweeper.View.TUI.REPL
import scala.io.StdIn.readLine

// $COVERAGE-OFF$
@main def run: Unit =
  new GUI().run
  new REPL().run

// $COVERAGE-ON$
