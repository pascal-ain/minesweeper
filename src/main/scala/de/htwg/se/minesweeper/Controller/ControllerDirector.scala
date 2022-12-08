package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Controller.Controller
import de.htwg.se.minesweeper.Model.Game

object ControllerDirector:
  def default =
    new Controller(Game(9, 9, 0.2))

  def defaultNumbers(mines: Int) =
    s"${(0 until 9).map(num => s"${num.toString}\uFE0F\u20E3")(mines)} "
