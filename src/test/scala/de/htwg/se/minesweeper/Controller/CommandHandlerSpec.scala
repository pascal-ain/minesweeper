package de.htwg.se.minesweeper.Controller

import de.htwg.se.minesweeper.Util.Helper
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Model.*

class CommandHandlerSpec extends AnyWordSpec {
  val game1 = Game(9, 9, 0.15)
  "A lost game" should {

    "be frozen" in {
      val mine = game1.board.mines.toVector(0)
      val lostGame = openField(game1, mine)

      openField(lostGame, Position(2, 2)) should equal(lostGame)
      flagField(lostGame, Position(2, 2)) should equal(lostGame)
    }
  }
  "When not lost the game" should {
    "work as expected" in {
      flagField(game1, Position(1, 1)).board.flaggedFields should contain(
        Position(1, 1)
      )

      val safeFields = (0 until game1.bounds.width)
        .flatMap(x => (0 until game1.bounds.height).map(Position(x, _)))
        .filterNot(game1.board.mines.contains(_))
      openField(game1, safeFields(0)).board.openFields.size should be >= 1
    }
  }
  "When won the game" should {
    "be frozen" in {
      val wonGame = Helper
        .openFields(
          game1,
          Helper
            .getAllPositions(game1)
            .filterNot(game1.board.mines.contains(_))
            .iterator
        )
        .won_?()
      openField(wonGame, Position(0, 0)) shouldBe wonGame
      flagField(wonGame, Position(0, 0)) shouldBe wonGame
    }
  }
}
