package de.htwg.se.minesweeper

import Model.GameComponent.GameInterface
import Model.GameComponent.GameBaseImplementation.Game
import Controller.ControllerInterface
import Controller.ControllerComponent.ControllerBaseImplementation
import Model.GameComponent.{Symbols, Score, Mine, Flag, Closed}
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOInterface
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOXMLImplementation
import de.htwg.se.minesweeper.Model.FileIOComponent.FileIOJSONImplementation

object Config {
  val dataPath = System.getProperty("user.dir") + "/data/"
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

  given FileIOInterface = FileIOJSONImplementation.FileIO()
  given GameInterface = game
  given ControllerInterface =
    ControllerBaseImplementation.Controller()
}
