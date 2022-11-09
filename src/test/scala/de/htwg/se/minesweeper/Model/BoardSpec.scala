package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.Helper

class BoardSpec extends AnyWordSpec {
  val game1 = Game(9, 10, 0.15)
  val game2 = Game(1, 2, 0)
  "Board is a helper class and" should {
    "have a static function that helps to generate random positions" in {
      generateRandomPositions(8, 9, 9).size shouldBe 8
      generateRandomPositions(10, 10, 10).size shouldBe 10
    }
    "have a method that gives the surrounding position of the input position" in {
      game1.board
        .getSurroundingPositions(Position(0, 0), game1.bounds)
        .toVector
        .size shouldBe 3 // top left only has 3 neighbors!
      game1.board
        .getSurroundingPositions(Position(2, 2), game1.bounds)
        .toVector
        .size shouldBe 8 // default case
      game1.board
        .getSurroundingPositions(Position(0, 5), game1.bounds)
        .toVector
        .size shouldBe 5 // on the edge
      game2.board
        .getSurroundingPositions(Position(0, 0), game2.bounds)
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
    game1.board.surroundingMines(nextToMine, game1.bounds) should be >= 1
    val noMines =
      Helper
        .getAllPositions(game1)
        .filter(game1.board.surroundingMines(_, game1.bounds) == 0)
        .next()
    game1.board.surroundingMines(noMines, game1.bounds) shouldBe 0
  }
}
