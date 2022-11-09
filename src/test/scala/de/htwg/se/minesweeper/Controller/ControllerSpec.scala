package de.htwg.se.minesweeper.Controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Util.*

class ControllerSpec extends AnyWordSpec {
  "The controller acts as a middleman between view and model and" should {
    "tell the view about success or possible errors" in {
      val game = Game(9, 9, 0.25)
      val controller = new Controller(game)
      controller.validatePosition(Position(0, 420), Field.Open) shouldBe a[
        Event.InvalidPosition
      ]
      controller
        .validatePosition(Position(0, 1), Field.Open) shouldBe a[Event.Success]
      controller.validatePosition(Position(0, 1), Field.Flag) shouldBe a[
        Event.InvalidPosition
      ]
    }
    "tell the view about a state change in the game" in {
      val game = Game(10, 10, 0.2)
      val controller = new Controller(game)
      controller.state shouldBe a[Event.Success]
      val notMines =
        Helper.getAllPositions(game).filterNot(game.board.mines.contains(_))
      notMines.foreach(controller.validatePosition(_, Field.Open))
      controller.state shouldBe Event.Won

      controller.validatePosition(game.board.mines.toVector(0), Field.Open)
      controller.state shouldBe Event.Lost
    }
    "have the same string representation as the model" in {
      val game = Game(10, 10, 0.2)
      val controller = new Controller(game)
      game.toString should equal(controller.toString)
    }
  }
}
