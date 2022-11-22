package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.Helper

class BoardSpec extends AnyWordSpec {
  val game1 = Game(9, 10, 0.15)
  val game2 = Game(1, 2, 0)
  "Board is a helper class and" should {
    "have a static function that helps to generate a board with random positions" in {
      generateRandomPositions(8, Bounds(8, 8)).mines.size shouldBe 8
      generateRandomPositions(10, Bounds(8, 9)).mines.size shouldBe 10
    }
    "have a method that gives the surrounding position of the input position" in {
      game1.board
        .getSurroundingPositions(Position(0, 0))
        .toVector
        .size shouldBe 3 // top left only has 3 neighbors!
      game1.board
        .getSurroundingPositions(Position(2, 2))
        .toVector
        .size shouldBe 8 // default case
      game1.board
        .getSurroundingPositions(Position(0, 5))
        .toVector
        .size shouldBe 5 // on the edge
      game2.board
        .getSurroundingPositions(Position(0, 0))
        .size shouldBe 1 // small field
    }
  }
  "have a method that counts the surrounding mines" in {
    val mine =
      Helper.getAllPositions(game1).filter(game1.board.mines.contains(_)).next()
    val nextToMine = {
      val (x, y) = (mine.x, mine.y)
      if (x + 1) > game1.bounds.width then Position(x - 1, y)
      else Position(x + 1, y)
    }
    game1.board.surroundingMines(nextToMine) should be >= 1
    val noMines =
      Helper
        .getAllPositions(game1)
        .filter(game1.board.surroundingMines(_) == 0)
        .next()
    game1.board.surroundingMines(noMines) shouldBe 0
  }
  "have a method that gets all positions of the board" in {
    val game1Pos = game1.board.getAllPositions
    game1Pos.length shouldBe 9 * 10
    game2.board.getAllPositions.length shouldBe 1 * 2

    game1Pos.contains(Position(0, 0)) shouldBe true
    game1Pos.contains(Position(1, 0)) shouldBe true
    game1Pos.contains(Position(8, 9)) shouldBe true
  }
}
