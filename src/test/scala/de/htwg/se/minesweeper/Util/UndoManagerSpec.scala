package de.htwg.se.minesweeper.Util

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class UndoManagerSpec extends AnyWordSpec {
  "An UndoManager" should {
    "only be able to undo when there is something to undo" in {
      val undoManager = new UndoManager[Int]
      val command = new IncrementCommand
      var state = 0
      undoManager.undoStep(state) shouldBe None
      val newState = undoManager.doStep(state, command)
      newState shouldBe 1

      undoManager.undoStep(newState) shouldBe Some(0)
    }
    "redo which is undoing an undo, doing something makes all past undos not redoable" in {
      val undoManager = new UndoManager[Int]
      val command = new IncrementCommand
      var state = 0
      undoManager.redoStep(state) shouldBe None
      val newState = undoManager.doStep(state, command) // 1
      val undid = undoManager.undoStep(newState) // 0
      val redid = undoManager.redoStep(undid.get) // 1
      redid shouldBe Some(1)

      undoManager.redoStep(redid.get) shouldBe None

      undoManager.doStep(state, command)
      undoManager.redoStep(state) shouldBe None
    }
    "handle multiple redos and undos correctly" in {
      val undoManager = new UndoManager[Int]
      val command = new IncrementCommand
      var state = 0
      state = undoManager.doStep(state, command)
      state = undoManager.doStep(state, command)
      state shouldBe 2
      state = undoManager.undoStep(state).get
      state shouldBe 1
      state = undoManager.undoStep(state).get
      state shouldBe 0

      state = undoManager.redoStep(state).get
      state shouldBe 1
    }
  }
}
