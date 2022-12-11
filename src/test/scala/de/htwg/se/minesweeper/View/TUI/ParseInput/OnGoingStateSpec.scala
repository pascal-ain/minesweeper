package de.htwg.se.minesweeper.View.TUI.ParseInput

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation.*
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.*
import scala.util.{Either, Left => Err, Right => Ok}

class OnGoingStrategySpec extends AnyWordSpec {
  val game = Game(9, 9, 0)
  val controller = new Controller(game)
  val parseOnGoing = OnGoingState(controller)
  "When the game is ongoing it" should {
    "accept exitting, opening, flagging, undoing and redoing the game" in {
      // exiting the game
      parseOnGoing.handleInput("exit") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("q ") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput(" quit") shouldBe a[Ok[_, _]]

      // opening
      parseOnGoing.handleInput("open 2 , 3") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("    open 4, 5") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("open 1,1") shouldBe a[Ok[_, _]]

      // flagging
      parseOnGoing.handleInput("flag 2 , 3") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("    flag 4, 5") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("flag 1,1") shouldBe a[Ok[_, _]]
      // undo redo
      parseOnGoing.handleInput("undo ") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("    redo ") shouldBe a[Ok[_, _]]
      parseOnGoing.handleInput("undo") shouldBe a[Ok[_, _]]
    }
    "reject incorrect input" in {
      parseOnGoing.handleInput("reeee") shouldBe a[Err[_, _]]
      parseOnGoing.handleInput("hacker 1337") shouldBe a[Err[_, _]]
      parseOnGoing.handleInput("flag") shouldBe a[Err[_, _]]
      parseOnGoing.handleInput("   open ") shouldBe a[Err[_, _]]
      parseOnGoing.handleInput("uuundo") shouldBe a[Err[_, _]]
      parseOnGoing.handleInput("20") shouldBe a[Err[_, _]]
    }
  }
}
