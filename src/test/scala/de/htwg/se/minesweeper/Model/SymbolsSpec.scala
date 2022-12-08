package de.htwg.se.minesweeper.Model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class SymbolsSpec extends AnyWordSpec {
  "Symbols are typed results of actions on the board they" should {
    "have a default string implementation" in {
      Flag.toString() shouldBe "F"
      Mine.toString() shouldBe "B"
      Closed.toString() shouldBe "?"
      Score(3).toString() shouldBe "3"
    }
  }
}
