package de.htwg.se.minesweeper

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet

class GameSpec extends AnyWordSpec {
  val eol = sys.props("line.separator")
  "The minesweeper game" should {
    val game1 = new Game(2, 4)
    val game2 = new Game(4, 2)
    val game3 = new Game(4, 4)
    "have a scalable width" in {
      game1.toString should startWith(" O  O " + eol)
      game2.toString should startWith(" O  O  O  O " + eol)
      game3.toString() should startWith(" O  O  O  O " + eol)
    }
    "have a scalable height" in {
      game1.toString should fullyMatch regex ("( O  O " + eol + "){4}")
      game2.toString should fullyMatch regex ("( O  O  O  O " + eol + "){2}")
      game3.toString should fullyMatch regex ("( O  O  O  O " + eol + "){4}")
    }
    "only have mines populated when created" in {
      game1.board.mines should not be empty
      game2.board.flaggedFields shouldBe empty
      game3.board.openFields shouldBe empty
    }
    "be ongoing (not lost) when created" in {
      game1.lost shouldBe false
      game2.lost shouldBe false
      game3.lost shouldBe false
    }
  }
}
