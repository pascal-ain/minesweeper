package de.htwg.se.minesweeper.Util

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class IncrementCommand extends Command[Int]:
  override def redoStep(state: Int) = state + 1
  override def stepOn(state: Int) = state + 1
  override def undoStep(state: Int) = state - 1

class CommandSpec extends AnyWordSpec {
  val command = new IncrementCommand
  "A command holds state and" should {
    "be able to do an action on the wrapped data" in {
      command.stepOn(0) shouldBe 1
      command.stepOn(1) shouldBe 2
    }
    "be able to undo" in {
      command.undoStep(1) shouldBe 0
      command.undoStep(0) shouldBe -1
    }
    "be able to redo" in {
      command.redoStep(-1) shouldBe 0
      command.redoStep(2) shouldBe 3
    }
  }
}
