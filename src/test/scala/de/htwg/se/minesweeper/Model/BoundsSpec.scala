package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.*

class BoundsSpec extends AnyWordSpec {
  "Bounds has two helper functions that" should {
    val game1 = Game(10, 10, 0.2)
    val game2 = Game(40, 20, 0.44)
    "return the size of the board" in {
      game1.bounds.size shouldBe 10 * 10
      game2.bounds.size shouldBe 40 * 20
    }
    "check if a given position is inside the board" in {
      game1.bounds.isInBounds(Position(0, 0)) shouldBe true
      game1.bounds.isInBounds(Position(420, 1337)) shouldBe false
      game2.bounds.isInBounds(Position(12, 1)) shouldBe true
    }
  }
}
