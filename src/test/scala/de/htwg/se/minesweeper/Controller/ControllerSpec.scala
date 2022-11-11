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
      val notMines =
        Helper.getAllPositions(game).filterNot(game.board.mines.contains(_))
      val won_game = Helper.openFields(game, notMines)
      val controller = new Controller(won_game)
      val result = new Controller(game).state
      result shouldBe a[Event.Success]

      controller.state shouldBe Event.Won

      controller.validatePosition(game.board.mines.toVector(0), Field.Open)
      controller.state shouldBe Event.Lost
    }
    "have the same string representation as the model" in {
      val game = Game(10, 10, 0.2)
      val controller = new Controller(game)
      game.toString should equal(controller.toString)
    }
    "notify Observers" in {
      val game = Game(10, 10, 0.2)
      val controller = new Controller(game)
      class TestObserver(c: Controller) extends Observer:
        c.add(this)
        var bing = Event.Won
        def update(e: Event): Unit = bing = e
      val testObserver = TestObserver(controller)
      testObserver.bing should be(Event.Won)
      controller.openField(Position(11, 11))
      testObserver.bing shouldBe a[Event.InvalidPosition]
      val notMine =
        Helper
          .getAllPositions(game)
          .filterNot(game.board.mines.contains(_))
          .toVector(0)

      controller.openField(notMine)
      testObserver.bing shouldBe a[Event.Success]

      val controller2 = Controller(game)
      val testObserver2 = TestObserver(controller2)
      testObserver2.bing should be(Event.Won)
      controller2.flagField(Position(1, 1))
      testObserver2.bing shouldBe a[Event.Success]
    }
  }
}
