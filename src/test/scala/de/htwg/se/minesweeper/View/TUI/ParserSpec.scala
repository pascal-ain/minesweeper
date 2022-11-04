package de.htwg.se.minesweeper.View.TUI

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import de.htwg.se.minesweeper.Model.*

class ParserSpec extends AnyWordSpec {
  val game = Game(9, 9, 0.15)
  "User input commands" should {
    "be separated by spaces" in {
      parse("open   1,1", game).get should equal(game.openField(Position(1, 1)))
      parse("flag     1,1", game).get should equal(
        game.flagField(Position(1, 1))
      )
      // parse("exit", game).get
    }
    "be rejected when malformed" in {
      parse("jdfhsdjfsdf 2221", game) shouldBe None
      parse("open   f212,1dg", game) shouldBe None
      parse("flag     ", game) shouldBe None
      parse("not ", game) shouldBe None
      parse("when 2,2", game) shouldBe None
      checkToken(Array("2", "bla", "bla"), game) shouldBe None

    }
  }
}
