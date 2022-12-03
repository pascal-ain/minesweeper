package de.htwg.se.minesweeper

import scala.util.{Success, Failure}
import de.htwg.se.minesweeper.View.ViewDirector
import de.htwg.se.minesweeper.View.GUI.GUI
import javax.swing.SwingUtilities

// $COVERAGE-OFF$
@main def run: Unit =
  ViewDirector.constructDefaultGUI.build match
    case Failure(exception) => println(exception)
    case Success(value)     => new GUI(value, "F", "M", "", (x: Int) => x.toString)

// $COVERAGE-ON$
