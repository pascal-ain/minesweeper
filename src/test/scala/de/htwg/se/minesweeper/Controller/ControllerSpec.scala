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
      controller
        .openField(
          Position(0, 420)
        ) shouldBe InsertResult.NotInBounds

      controller
        .openField(Position(0, 1)) shouldBe a[InsertResult.Success]

      controller.handleTrigger(controller.openField, Position(0, 1))
      controller.flagField(
        Position(0, 1)
      ) shouldBe InsertResult.AlreadyOpen

      val flagged = new Controller(game)
      flagged.handleTrigger(flagged.flagField, Position(0, 0))
      flagged.openField(Position(0, 0)) shouldBe InsertResult.Flagged
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
      controller.handleTrigger(
        controller.openField,
        controller.game.board.mines.toVector(0)
      )

      controller.state shouldBe Event.Lost
    }
    "have the same string representation as the model" in {
      val game = Game(10, 10, 0.2)
      val controller = new Controller(game)
      game.toString should equal(controller.toString)
    }
    "notify Observers" in {
      val game = Game(10, 10, 0.2)
      val controllerToOpen = new Controller(game)
      class TestObserver(c: Controller) extends Observer:
        c.add(this)
        var bing = Event.Won
        def update(e: Event): Unit = bing = e

      val testOpen = TestObserver(controllerToOpen)
      testOpen.bing should be(Event.Won)

      controllerToOpen.handleTrigger(
        controllerToOpen.openField,
        Position(11, 11)
      )
      testOpen.bing shouldBe a[Event.InvalidPosition]

      val notMine =
        Helper
          .getAllPositions(game)
          .filterNot(game.board.mines.contains(_))
          .toVector(0)

      controllerToOpen.handleTrigger(controllerToOpen.openField, notMine)
      testOpen.bing shouldBe a[Event.Success]

      controllerToOpen.handleTrigger(controllerToOpen.openField, notMine)
      testOpen.bing shouldBe a[Event.InvalidPosition]

      val controllerToFlag = Controller(game)
      val testFlagging = TestObserver(controllerToFlag)
      testFlagging.bing shouldBe Event.Won

      controllerToFlag.handleTrigger(
        controllerToFlag.flagField,
        Position(1337, 1)
      )
      testFlagging.bing shouldBe a[Event.InvalidPosition]

      controllerToFlag.handleTrigger(controllerToFlag.flagField, Position(1, 1))
      testFlagging.bing shouldBe a[Event.Success]

      controllerToFlag.handleTrigger(controllerToFlag.openField, Position(1, 1))
      testFlagging.bing shouldBe a[Event.InvalidPosition]
    }
  }
}
