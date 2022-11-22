package de.htwg.se.minesweeper

import scala.util.{Success, Failure}
import de.htwg.se.minesweeper.View.ViewDirector

// $COVERAGE-OFF$
@main def run: Unit =
  ViewDirector.constructDefaultTUI.build match
    case Failure(exception) => println(exception)
    case Success(value)     => value.run()

// $COVERAGE-ON$
