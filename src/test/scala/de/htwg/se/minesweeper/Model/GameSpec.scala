package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet

class GameSpec extends AnyWordSpec {
  val eol = sys.props("line.separator")
  def calculateMines(width: Int, height: Int, percentage: Double) =
    (width * height * percentage).toInt
  val game1 = Game(2, 4, 0.15)
  val game2 = Game(4, 2, 0.30)
  val game3 = Game(4, 4, 0.50)
  "The minesweeper game" should {
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
    "have the correct symbols" in {
      val mines = game1.board.surroundingMines(Position(0, 0), game1.bounds)
      game1.openField(Position(0, 0)).toString() should include regex (
        s"[$mines¤]"
      )
      game2.flagField(Position(0, 0)).toString() should startWith(
        " F  O  O  O " + eol
      )
      val mines3 = game3.board.surroundingMines(Position(2, 0), game3.bounds)
      game3
        .openField(Position(2, 0))
        .openField(Position(2, 0))
        .toString() should startWith regex (s" O  O  [¤$mines3]  O " + eol)
      val mine = game1.board.mines.toVector(0)
      game1.openField(mine).toString should include(" ¤ ")
    }
    "have a scalable minecount" in {
      game1.board.mines.size shouldBe calculateMines(2, 4, 0.15)
      game2.board.mines.size shouldBe calculateMines(4, 2, 0.30)
      game3.board.mines.size shouldBe calculateMines(4, 4, 0.50)
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
