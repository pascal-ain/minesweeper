package de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.*
import de.htwg.se.minesweeper.Util.*
import de.htwg.se.minesweeper.Model.*

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
        )
        .isLeft shouldBe true

      controller
        .openField(Position(0, 1))
        .isRight shouldBe true
      val notMine =
        game.board.getAllPositions.filterNot(game.board.mines.contains(_)).next
      controller.handleTrigger(controller.openField, notMine)
      controller
        .flagField(
          notMine
        )
        .isLeft shouldBe true

      val flagged = game.flagField(Position(0, 0))
      flagged.canOpen_?(Position(0, 0)) shouldBe InsertResult.Flagged

      // this should never happen
      controller.handleError(InsertResult.Ok).isLeft shouldBe true
    }
    "tell the view about a state change in the game" in {
      val game = Game(10, 10, 0.2)
      val notMines =
        Helper.getAllPositions(game).filterNot(game.board.mines.contains(_))
      val won_game = Helper.openFields(game, notMines)
      val controller = new Controller(won_game)
      val result = new Controller(game).state
      result shouldBe Event.Success

      controller.state shouldBe Event.Won
      controller.handleTrigger(
        controller.openField,
        controller.game.getSnapShot.mines.toVector(0)
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
      testOpen.bing shouldBe Event.Success

      val controllerToFlag = Controller(game)
      val testFlagging = TestObserver(controllerToFlag)
      testFlagging.bing shouldBe Event.Won

      controllerToFlag.handleTrigger(
        controllerToFlag.flagField,
        Position(1337, 1)
      )
      testFlagging.bing shouldBe a[Event.Failure]

      controllerToFlag.handleTrigger(controllerToFlag.flagField, Position(1, 1))
      testFlagging.bing shouldBe Event.Success

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
      controller.undo() shouldBe Left(Undo)
      controller.redo() shouldBe Left(Redo)

      val notMine = game.board.getAllPositions
        .filterNot(game.board.mines.contains(_))
        .next()
      val observing = new TestObserver(controller)
      controller.handleTrigger(controller.openField, notMine)
      observing.bing shouldBe Event.Success

      controller.handleTrigger(controller.redo)
      observing.bing shouldBe a[Event.Failure]

      controller.handleTrigger(controller.undo)
      observing.bing shouldBe Event.Success
      controller.handleTrigger(controller.undo)
      observing.bing shouldBe a[Event.Failure]

      controller.handleTrigger(controller.redo)
      observing.bing shouldBe Event.Success

      controller.handleTrigger(controller.undo)
      observing.bing shouldBe Event.Success

      controller.handleTrigger(controller.flagField, Position(1, 1))
      observing.bing shouldBe Event.Success

      controller.handleTrigger(controller.redo)
      observing.bing shouldBe a[Event.Failure]

      controller.handleTrigger(controller.undo)
      observing.bing shouldBe Event.Success
      controller.handleTrigger(controller.redo)
      observing.bing shouldBe Event.Success
    }
    "only undo the previous opened fields" in {
      val game = Game(20, 20, 0.2)
      val modified =
        game.copy(board =
          game.board.copy(mines = Set(Position(0, 0), Position(4, 4)))
        )
      val controller = new Controller(modified)
      val notMine = Position(1, 1)
      val zeroField = Position(9, 9)
      controller.handleTrigger(controller.openField, notMine)
      controller.game.getSnapShot.openFields.contains(notMine) shouldBe true

      controller.handleTrigger(controller.openField, zeroField)
      controller.handleTrigger(controller.undo)
      controller.game.getSnapShot.openFields.contains(notMine) shouldBe true
      controller.game.getSnapShot.openFields.contains(zeroField) shouldBe false
    }
    "change the state correctly on redo and undo" in {
      var game = Game(9, 11, 0.2)
      val controller = new Controller(game)

      val mine = game.board.mines.iterator.next()
      controller.handleTrigger(controller.openField, mine)
      controller.state shouldBe Event.Lost

      controller.handleTrigger(controller.undo)
      controller.state shouldBe Event.Success
      controller.handleTrigger(controller.redo)
      controller.state shouldBe Event.Lost

      controller.handleTrigger(controller.undo)

      val modified = game.copy(board =
        game.board.copy(mines = Set(Position(0, 0), Position(1, 1)))
      )
      val missingField = Position(0, 1)
      val almostWon = Helper.openFields(
        modified,
        modified.board.getAllPositions.filter(pos =>
          !modified.board.mines.contains(pos) && pos != missingField
        )
      )

      val win = new Controller(almostWon)
      win.handleTrigger(win.openField, missingField)
      win.state shouldBe Event.Won
      win.handleTrigger(win.undo)
      win.state shouldBe Event.Success
      win.handleTrigger(win.redo)
      win.state shouldBe Event.Won
    }
  }
}
