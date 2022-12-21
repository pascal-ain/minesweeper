package de.htwg.se.minesweeper

import de.htwg.se.minesweeper.Model.GameInterface
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.Game
import de.htwg.se.minesweeper.Controller.ControllerInterface
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation

object Config {
  var game = Game(10, 10, 0.2)
  given GameInterface = game
  given ControllerInterface =
    ControllerBaseImplementation.Controller()
}
