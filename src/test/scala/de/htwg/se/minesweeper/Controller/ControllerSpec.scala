package de.htwg.se.minesweeper.Controller

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import de.htwg.se.minesweeper.Model.*
import de.htwg.se.minesweeper.Util.*

class ControllerSpec extends AnyWordSpec {
  "The controller acts as a middleman between view and model and" should {
    class TestObserver(c: Controller) extends Observer:
      c.add(this)
      var bing = Event.Won
      def update(e: Event): Unit = bing = e
    "tell the view about success or possible errors" in {
      val game = Game(9, 9, 0.25)
      val controller = new Controller(game)
      controller
        .openField(
          Position(0, 420)
        ) shouldBe InsertResult.NotInBounds

      controller
        .openField(Position(0, 1)) shouldBe a[InsertResult.Success]

      val notMine =
        game.board.getAllPositions.filterNot(game.board.mines.contains(_)).next
      controller.handleTrigger(controller.openField, notMine)
      controller.flagField(
        notMine
      ) shouldBe InsertResult.AlreadyOpen

      val flagged = game.toggleFlag(Position(0, 0))
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

      val testOpen = TestObserver(controllerToOpen)
      testOpen.bing should be(Event.Won)

      controllerToOpen.handleTrigger(
        controllerToOpen.openField,
        Position(11, 11)
      )
      testOpen.bing shouldBe a[Event.Failure]

      val notMine =
        Helper
          .getAllPositions(game)
          .filterNot(game.board.mines.contains(_))
          .next()

      controllerToOpen.handleTrigger(controllerToOpen.openField, notMine)
      testOpen.bing shouldBe a[Event.Success]

      val controllerToFlag = Controller(game)
      val testFlagging = TestObserver(controllerToFlag)
      testFlagging.bing shouldBe Event.Won

      controllerToFlag.handleTrigger(
        controllerToFlag.flagField,
        Position(1337, 1)
      )
      testFlagging.bing shouldBe a[Event.Failure]

      controllerToFlag.handleTrigger(controllerToFlag.flagField, Position(1, 1))
      testFlagging.bing shouldBe a[Event.Success]

      controllerToFlag.handleTrigger(controllerToFlag.openField, Position(1, 1))
      testFlagging.bing shouldBe a[Event.Failure]
    }
    "hold a vector of subscribers that can be added or removed" in {
      val game = Game(9, 12, 0.3)
      val controller = new Controller(game)
      val test = new TestObserver(controller)
      val initEvent = test.bing
      controller.handleTrigger(controller.openField, Position(0, 0))
      val afterOpen = test.bing
      initEvent shouldNot be(afterOpen)

      controller.remove(test)
      controller.handleTrigger(controller.openField, Position(0, 0))
      test.bing shouldBe afterOpen // unsubbed the observer so it shouldn't get new messages
    }
    "undo or redo and report about possible errors" in {
      var game = Game(9, 11, 0.2)
      val controller = new Controller(game)

      // nothing to redo or undo at the beginning
      controller.undo shouldBe Left(Undo)
      controller.redo shouldBe Left(Redo)

      val notMine = game.board.getAllPositions
        .filterNot(game.board.mines.contains(_))
        .next()
      val observing = new TestObserver(controller)
      controller.handleTrigger(controller.openField, notMine)
      observing.bing shouldBe a[Event.Success]

      controller.handleTrigger(controller.redo)
      observing.bing shouldBe a[Event.Failure]

      controller.handleTrigger(controller.undo)
      observing.bing shouldBe a[Event.Success]
      controller.handleTrigger(controller.undo)
      observing.bing shouldBe a[Event.Failure]

      controller.handleTrigger(controller.redo)
      observing.bing shouldBe a[Event.Success]

      controller.handleTrigger(controller.undo)
      observing.bing shouldBe a[Event.Success]

      controller.handleTrigger(controller.flagField, Position(1, 1))
      observing.bing shouldBe a[Event.Success]

      controller.handleTrigger(controller.redo)
      observing.bing shouldBe a[Event.Failure]

      controller.handleTrigger(controller.undo)
      observing.bing shouldBe a[Event.Success]
      controller.handleTrigger(controller.redo)
      observing.bing shouldBe a[Event.Success]

    }
  }
}
