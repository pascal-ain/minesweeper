package de.htwg.se.minesweeper

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet

class ModelSpec extends AnyWordSpec {
    val eol = sys.props("line.separator")
    "The new minesweeper game" should {
        val game1 = new Game(2, 4)
        val game2 = new Game(4, 2)
        val game3 = new Game(4, 4)
        "have a scalable width" in {
            game1.toString should startWith(" O  O " + eol)
            game2.toString should startWith(" O  O  O  O " + eol)
            game3.toString() should startWith(" O  O  O  O " + eol)
        }
        "have a scalable height" in {
            game1.toString should fullyMatch regex("( O  O " + eol + "){4}")
            game2.toString should fullyMatch regex("( O  O  O  O " + eol + "){2}")
            game3.toString should fullyMatch regex("( O  O  O  O " + eol + "){4}")
        }
        "only have mines populated" in {
            game1.board.mines should not be empty
            game2.board.flaggedFields shouldBe empty
            game3.board.openFields shouldBe empty
            game2.lost shouldBe false
        }
    }

    "The new minesweeper fields" should {
        val game1 = new Game(2, 4)
        val game2 = new Game(4, 2)
        val game3 = new Game(4, 4)
        "be 15% mines at random positions" in {
            game1.generateBoard.mines.size shouldBe (2*4*MINE_PERCENTAGE).floor.toInt
            game2.generateBoard.mines.size shouldBe (4*2*MINE_PERCENTAGE).floor.toInt
            game3.generateBoard.mines.size shouldBe (4*4*MINE_PERCENTAGE).floor.toInt

            game1.randomPositions(1, HashSet.empty).size shouldBe 1
            game2.randomPositions(8, HashSet().empty).size shouldBe 8
            game3.randomPositions(12, HashSet.empty).size shouldBe 12
        }
        "be displayed with their assigned symbols" in {
            game1.whichSymbol(Position(1, 2)) shouldBe " O "
            game2.whichSymbol(Position(2, 3)) shouldBe " O "
            game3.whichSymbol(Position(3, 3)) shouldBe " O "
        }
    }
}