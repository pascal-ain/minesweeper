package de.htwg.se.minesweeper

import scala.util.{Success, Failure}

import de.htwg.se.minesweeper.View.{ViewFactory, InterfaceType}
import de.htwg.se.minesweeper.Controller.ControllerComponent.*

// $COVERAGE-OFF$
@main def run: Unit =
  val controller = ControllerDirector.defaultController().build().get

  ViewFactory(InterfaceType.GUI)(controller).run
  ViewFactory(InterfaceType.TUI)(controller).run

// $COVERAGE-ON$
