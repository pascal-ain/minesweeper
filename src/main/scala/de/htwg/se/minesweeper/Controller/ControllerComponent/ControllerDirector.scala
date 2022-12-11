package de.htwg.se.minesweeper.Controller.ControllerComponent

// import de.htwg.se.minesweeper.Controller.ControllerInterface

object ControllerDirector:
  def defaultController(): GameBuilder =
    ControllerBuilder().width(9).height(9).mines(0.15)
