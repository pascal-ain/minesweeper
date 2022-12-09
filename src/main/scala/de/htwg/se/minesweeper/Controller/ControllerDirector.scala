package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Controller.Controller

object ControllerDirector:
  def defaultController(): GameBuilder =
    ControllerBuilder().width(9).height(9).mines(0.15)
