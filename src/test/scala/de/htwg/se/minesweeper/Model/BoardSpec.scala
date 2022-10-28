package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import scala.collection.immutable.HashSet

class BoardSpec extends AnyWordSpec {
  val game1 = new Game(2, 4, 0.15)
  val game2 = new Game(4, 2, 0.30)
  val game3 = new Game(4, 4, 0.50)
  "Fields on the board" should {
    val game1Open = game1.openField(Position(1, 1))
    val game2Open = game2.openField(Position(2, 1))
    val game3Open = game3.openField(Position(5, 3))
    "be openable and ignore invalid positions" in {
      game1Open.board.openFields should contain(Position(1, 1))
      game2.openField(Position(2, 1)).board.openFields.size shouldBe 1
      game3Open.board.openFields should not contain (Position(5, 3))
      game1
        .flagField(Position(1, 1))
        .openField(Position(1, 1))
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
      game2.flagField(Position(1, 1)).board.flaggedFields should contain(
        Position(1, 1)
      )
      game3.flagField(Position(2, 7)).board.flaggedFields.size shouldBe 0
      game1Open.flagField(Position(1, 1)).board.flaggedFields.size shouldBe 0
    }
  }
}