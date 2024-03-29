package de.htwg.se.minesweeper

import Model.GameComponent.GameBaseImplementation.Game
import Controller.ControllerComponent.{
  ControllerInterface,
  ControllerBaseImplementation
}
import Model.GameComponent.{Symbols, Score, Mine, Flag, Closed, GameInterface}
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOTOMLImplementation.FileIO

object Config {
  val defaultFileImplementation = FileIO
  val defaultFileExtension = "toml"

  def imagePath(symbol: Symbols) =
    symbol match
      case Closed          => "closedField.png"
      case Flag            => "flag.png"
      case Mine            => "mine.png"
      case Score(num: Int) => s"$num.png"

  val minePercentage = 0.2
  val defaultHeight = 10
  val defaultWidth = 10
  var game = Game(defaultWidth, defaultHeight, minePercentage)

  def newGame(width: Int, height: Int, mines: Double) =
    Game(width, height, mines)
  given GameInterface = game
  given ControllerInterface =
    ControllerBaseImplementation.Controller()
}
