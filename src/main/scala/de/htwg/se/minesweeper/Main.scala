package de.htwg.se.minesweeper

import scala.util.{Success, Failure}

import de.htwg.se.minesweeper.View.GUI.GUI
import javax.swing.SwingUtilities
import de.htwg.se.minesweeper.Controller.ControllerComponent.*
import de.htwg.se.minesweeper.View.TUI.REPL

// $COVERAGE-OFF$
@main def run: Unit =
  val controller = ControllerDirector.defaultController().build().get

  new GUI(controller)
  new REPL(
    controller,
    flagSymbol = "[F]",
    mineSymbol = "[*]",
    closedFieldSymbol = "[?]",
    (x: Int) => s"${(0 until 9).map(num => s"[${num.toString}]")(x)}"
  ).run()

// $COVERAGE-ON$
