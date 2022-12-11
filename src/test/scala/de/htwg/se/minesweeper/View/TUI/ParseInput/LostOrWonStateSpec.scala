package de.htwg.se.minesweeper.View.TUI.ParseInput

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Controller.ControllerComponent.ControllerBaseImplementation.*
import de.htwg.se.minesweeper.Model.GameComponent.GameBaseImplementation.*
import scala.util.{Either, Left => Err, Right => Ok}

class LostOrWonStrategySpec extends AnyWordSpec {
  val game = Game(10, 10, 0)
  val controller = Controller(game)
  val parseInput = LostOrWonState(controller)
  "When the game is lost or won it" should {
    "only accept undo, exit or redo" in {
      parseInput.handleInput("open 1,1") shouldBe a[Left[_, _]]
      parseInput.handleInput("flag 1,1") shouldBe a[Left[_, _]]

      parseInput.handleInput(" undo ") shouldBe a[Right[_, _]]
      parseInput.handleInput(" redo") shouldBe a[Right[_, _]]
    }
    "reject malformed input" in {
      parseInput.handleInput("reeee") shouldBe a[Err[_, _]]
      parseInput.handleInput("hacker 1337") shouldBe a[Err[_, _]]
      parseInput.handleInput("flag") shouldBe a[Err[_, _]]
      parseInput.handleInput("   open ") shouldBe a[Err[_, _]]
      parseInput.handleInput("uuundo") shouldBe a[Err[_, _]]
      parseInput.handleInput("20") shouldBe a[Err[_, _]]
    }
  }
}
