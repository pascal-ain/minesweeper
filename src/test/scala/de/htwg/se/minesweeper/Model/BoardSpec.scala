package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet
import de.htwg.se.minesweeper.Util.Helper

class BoardSpec extends AnyWordSpec {
  val game1 = Game(2, 4, 0.15)
  val game2 = Game(4, 2, 0.30)
  val game3 = Game(4, 4, 0.50)
  val pos1_1 = Position(1, 1)
  "Fields on the board" should {
    val game1Open = game1.openField(pos1_1)
    val game2Open = game2.openField(Position(2, 1))
    val game3Open = game3.openField(Position(5, 3))
    "be openable and ignore invalid positions" in {
      game1Open.board.openFields should contain(
        pos1_1 -> game1Open.board
          .surroundingMines(pos1_1, game1Open.bounds)
      )
      game2.openField(Position(2, 1)).board.openFields.size should be >= 1
      game3Open.board.openFields should not contain (Position(5, 3))
      game1
        .flagField(pos1_1)
        .openField(pos1_1)
        .board
        .openFields
        .size shouldBe 0
    }
    "be flaggable and ignore invalid positions" in {
      val flagged = game1.flagField(Position(1, 3))
      flagged.board.flaggedFields should contain(Position(1, 3))
      flagged.flagField(Position(1, 3)).board.flaggedFields shouldNot contain(
        Position(1, 3)
      )
      game2.flagField(pos1_1).board.flaggedFields should contain(
        pos1_1
      )
      game3
        .flagField(Position(2, 7))
        .board
        .flaggedFields should not contain (Position(2, 7))
      game1Open
        .flagField(pos1_1)
        .board
        .flaggedFields should not contain pos1_1
    }
    "be opened when it is save to do so" in {
      val bigGame = Game(20, 20, 0.1)
      val safeFields = Helper
        .getAllPositions(bigGame)
        .filter(bigGame.board.surroundingMines(_, bigGame.bounds) == 0)
        .toVector
      bigGame.openField(safeFields(0)).board.openFields.size should be >= 3
    }
  }
}
