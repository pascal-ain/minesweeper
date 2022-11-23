package de.htwg.se.minesweeper.Util

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class UndoManagerSpec extends AnyWordSpec {
  "An UndoManager" should {
    "only be able to undo when there is something to undo" in {
      val undoManager = new UndoManager[Int]
      val command = new IncrementCommand
      var state = 0
      undoManager.undoStep(state) shouldBe Left(Undo)
      val newState = undoManager.doStep(state, command)
      newState shouldBe 1

      undoManager.undoStep(newState) shouldBe Right(0)
    }
    "redo which is undoing an undo, doing something makes all past undos not redoable" in {
      val undoManager = new UndoManager[Int]
      val command = new IncrementCommand
      var state = 0
      undoManager.redoStep(state) shouldBe Left(Redo)
      val newState = undoManager.doStep(state, command) // 1
      val undid = undoManager.undoStep(newState) // 0
      val redid =
        undoManager.redoStep(undid.getOrElse(throw new Exception())) // 1
      redid shouldBe Right(1)

      undoManager.redoStep(
        redid.getOrElse(throw new Exception())
      ) shouldBe Left(Redo)

      undoManager.doStep(state, command)
      undoManager.redoStep(state) shouldBe Left(Redo)
    }
    "handle multiple redos and undos correctly" in {
      val undoManager = new UndoManager[Int]
      val command = new IncrementCommand
      var state = 0
      state = undoManager.doStep(state, command)
      state = undoManager.doStep(state, command)
      state shouldBe 2
      state = undoManager.undoStep(state).getOrElse(throw new Exception())
      state shouldBe 1
      state = undoManager.undoStep(state).getOrElse(throw new Exception())
      state shouldBe 0

      state = undoManager.redoStep(state).getOrElse(throw new Exception())
      state shouldBe 1
    }
  }
}
